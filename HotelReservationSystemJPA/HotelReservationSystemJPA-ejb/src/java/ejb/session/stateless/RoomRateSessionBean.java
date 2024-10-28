/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.enumeration.RoomRateTypeEnum;
import util.exception.InvalidRoomRateException;
import util.exception.InvalidRoomRateNameException;
import util.exception.RoomRateAlreadyExistException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author JorJo
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

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

}
