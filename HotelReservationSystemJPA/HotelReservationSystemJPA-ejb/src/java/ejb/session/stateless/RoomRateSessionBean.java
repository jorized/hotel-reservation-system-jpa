/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.ReservationTypeEnum;
import util.enumeration.RoomRateStatusEnum;
import util.enumeration.RoomRateTypeEnum;
import util.exception.InvalidRoomRateException;
import util.exception.InvalidRoomRateNameException;
import util.exception.ReservationInUseException;
import util.exception.RoomRateAlreadyExistException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author JorJo
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public RoomRate createNewRoomRate(RoomRate newRoomRate) throws RoomRateAlreadyExistException {

        // Check if a room type with the same name already exists
        RoomRate existingRoomRate = em.createQuery("SELECT rR FROM RoomRate rR WHERE rR.rateName = :rateName", RoomRate.class)
                .setParameter("rateName", newRoomRate.getRateName())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existingRoomRate != null) {
            throw new RoomRateAlreadyExistException("Room rate '" + newRoomRate.getRateName() + "' already exists.");
        }

        em.persist(newRoomRate);
        em.flush();

        return newRoomRate;
    }

    @Override
    public RoomRate retrieveRoomRateByRoomName(String rateName) throws InvalidRoomRateNameException {
        try {
            return em.createQuery("SELECT rR FROM RoomRate rR WHERE rR.rateName = :rateName", RoomRate.class)
                    .setParameter("rateName", rateName)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidRoomRateNameException("Invalid room rate name");
        }
    }

    @Override
    public List<RoomRate> retrieveAllRoomRatesByRoomType(RoomType roomType) {
        return em.createQuery("SELECT rR FROM RoomRate rR WHERE rR.roomType = :roomType", RoomRate.class)
                .setParameter("roomType", roomType)
                .getResultList();
    }

    @Override
    public RoomRate updateRoomRate(RoomRate updatedRoomRate) throws UpdateRoomRateException {
        try {
            RoomRate existingRoomRate = em.find(RoomRate.class, updatedRoomRate.getRoomRateId());

            if (updatedRoomRate.getRoomType() != null) {
                existingRoomRate.setRoomType(updatedRoomRate.getRoomType());
            }
            if (updatedRoomRate.getRateType() != null) {
                existingRoomRate.setRateType(updatedRoomRate.getRateType());
            }

            // these dates may be null
            existingRoomRate.setPromotionStartDate(updatedRoomRate.getPromotionStartDate());

            existingRoomRate.setPromotionEndDate(updatedRoomRate.getPromotionEndDate());

            existingRoomRate.setPeakStartDate(updatedRoomRate.getPeakStartDate());

            existingRoomRate.setPeakEndDate(updatedRoomRate.getPeakEndDate());

            if (updatedRoomRate.getRatePerNight() != null) {
                existingRoomRate.setRatePerNight(updatedRoomRate.getRatePerNight());
            }

            em.merge(existingRoomRate);
            em.flush();

            return existingRoomRate;
        } catch (Exception e) {
            throw new UpdateRoomRateException("Error updating room rate details: " + e.getMessage());
        }

    }

    @Override
    public void deleteRoomRate(RoomRate existingRoomRate) throws ReservationInUseException {
        //Check if existing room rate is linked to any reservations. (Get all rooms linked to that room type)

        RoomRate managedRoomRate = em.find(RoomRate.class, existingRoomRate.getRoomRateId());

        if (reservationSessionBeanLocal.retrieveAllReservationsByRoomRate(managedRoomRate).isEmpty()) {
            em.remove(managedRoomRate);
            em.flush();
        } else {
            throw new ReservationInUseException("Room type is currently in use. Unable to delete.");
        }

    }

    @Override
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT r FROM RoomRate r");

        return query.getResultList();

    }

    @Override
    public BigDecimal getDailyRate(Date date, RoomType roomType, ReservationTypeEnum reservationTypeEnum) {
        BigDecimal rate = null;
        List<RoomRate> roomRates = retrieveAllRoomRates();

        for (RoomRate roomRate : roomRates) {
            if (roomRate.getRoomRateStatus() == RoomRateStatusEnum.ACTIVE && roomRate.getRoomType().equals(roomType)) {
                // Online reservations: Promotion -> Peak -> Normal priority
                if (reservationTypeEnum == ReservationTypeEnum.ONLINE) {
                    if (roomRate.getRateType() == RoomRateTypeEnum.PROMOTION
                            && isWithinPeriod(date, roomRate.getPromotionStartDate(), roomRate.getPromotionEndDate())) {
                        return roomRate.getRatePerNight(); // Highest priority
                    }
                    if (roomRate.getRateType() == RoomRateTypeEnum.PEAK
                            && isWithinPeriod(date, roomRate.getPeakStartDate(), roomRate.getPeakEndDate())) {
                        rate = roomRate.getRatePerNight(); // Next priority if no promotion is available
                    }
                    if (roomRate.getRateType() == RoomRateTypeEnum.NORMAL && rate == null) {
                        rate = roomRate.getRatePerNight(); // Default rate if neither promotion nor peak is available
                    }
                }

                // Walk-in reservations: Published rate only
                if (reservationTypeEnum == ReservationTypeEnum.WALKIN
                        && roomRate.getRateType() == RoomRateTypeEnum.PUBLISHED) {
                    return roomRate.getRatePerNight();
                }
            }
        }

        return rate;
    }

    @Override
    public RoomRate getDailyRateRoomRate(Date date, RoomType roomType, ReservationTypeEnum reservationTypeEnum) {
        RoomRate selectedRoomRate = null;
        List<RoomRate> roomRates = retrieveAllRoomRates();

        for (RoomRate roomRate : roomRates) {
            if (roomRate.getRoomRateStatus() == RoomRateStatusEnum.ACTIVE && roomRate.getRoomType().equals(roomType)) {
                
                if (reservationTypeEnum == ReservationTypeEnum.ONLINE) {
                    if (roomRate.getRateType() == RoomRateTypeEnum.PROMOTION
                            && isWithinPeriod(date, roomRate.getPromotionStartDate(), roomRate.getPromotionEndDate())) {
                        return roomRate; 
                    }
                    if (roomRate.getRateType() == RoomRateTypeEnum.PEAK
                            && isWithinPeriod(date, roomRate.getPeakStartDate(), roomRate.getPeakEndDate())) {
                        selectedRoomRate = roomRate; 
                    }
                    if (roomRate.getRateType() == RoomRateTypeEnum.NORMAL && selectedRoomRate == null) {
                        selectedRoomRate = roomRate; 
                    }
                }

                if (reservationTypeEnum == ReservationTypeEnum.WALKIN
                        && roomRate.getRateType() == RoomRateTypeEnum.PUBLISHED) {
                    return roomRate;
                }
            }
        }

        return selectedRoomRate;
    }

    private boolean isWithinPeriod(Date date, Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }

        return !date.before(startDate) && !date.after(endDate);
    }

    public RoomRate getRoomRateForType(RoomType roomType, Date currentDate) throws InvalidRoomRateException {
        List<RoomRate> roomRates = retrieveAllRoomRates();

        for (RoomRate roomRate : roomRates) {
            // Ensure rate is active and matches the room type
            if (roomRate.getRoomRateStatus() == RoomRateStatusEnum.ACTIVE && roomRate.getRoomType().equals(roomType)) {

                // Priority 1: Promotion rate
                if (roomRate.getRateType() == RoomRateTypeEnum.PROMOTION
                        && isWithinPeriod(currentDate, roomRate.getPromotionStartDate(), roomRate.getPromotionEndDate())) {
                    return roomRate;

                    // Priority 2: Peak rate
                } else if (roomRate.getRateType() == RoomRateTypeEnum.PEAK
                        && isWithinPeriod(currentDate, roomRate.getPeakStartDate(), roomRate.getPeakEndDate())) {
                    return roomRate;

                    // Priority 3: Normal rate
                } else if (roomRate.getRateType() == RoomRateTypeEnum.NORMAL) {
                    return roomRate;
                }
            }
        }
        throw new InvalidRoomRateException("No valid rate found for RoomType: " + roomType.getTypeName() + " on date: " + currentDate);

    }

}
