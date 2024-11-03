/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.ReservationTypeEnum;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomTypeCapacityException;
import util.exception.InvalidRoomTypeException;
import util.exception.InvalidRoomTypeNameException;
import util.exception.InvalidRoomTypeTierNumberException;
import util.exception.RoomTypeAlreadyExistException;
import util.exception.RoomTypeInUseException;

/**
 *
 * @author JorJo
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    @Override
    public RoomType createNewRoomType(RoomType newRoomType) throws RoomTypeAlreadyExistException, InvalidRoomTypeTierNumberException {

        // Check if a room type with the same name already exists
        RoomType existingRoomType = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.typeName = :typeName", RoomType.class)
                .setParameter("typeName", newRoomType.getTypeName())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existingRoomType != null) {
            throw new RoomTypeAlreadyExistException("Room type '" + newRoomType.getTypeName() + "' already exists.");
        }

        Integer currentHighestTierNumber = getHighestTierNumber();
        Integer tierNumber = newRoomType.getTierNumber();

        if (tierNumber < 1 || tierNumber > currentHighestTierNumber + 1) {
            throw new InvalidRoomTypeTierNumberException("Invalid tier number. Tier number must be from 1 to " + (currentHighestTierNumber + 1));
        }

        if (tierNumber <= currentHighestTierNumber) {
            // Shift existing room types up to accommodate the new tier number
            shiftTierNumberUpForCreate(tierNumber);
        }

        em.persist(newRoomType);
        em.flush();

        return newRoomType;
    }

    @Override
    public RoomType updateRoomType(RoomType updatedRoomType) throws InvalidRoomTypeTierNumberException, InvalidRoomTypeCapacityException {

        RoomType existingRoomType = em.find(RoomType.class, updatedRoomType.getRoomTypeId());

        Integer currentHighestTierNumber = getHighestTierNumber();
        Integer newTierNumber = updatedRoomType.getTierNumber();
        Integer currentTierNumber = existingRoomType.getTierNumber();

        // Update tier number if a new valid tier is provided and different from the current one
        if (newTierNumber != null && !newTierNumber.equals(currentTierNumber)) {
            if (newTierNumber < 1 || newTierNumber > currentHighestTierNumber) {
                throw new InvalidRoomTypeTierNumberException("Invalid tier number. Tier number must be from 1 to " + currentHighestTierNumber);
            }

            if (newTierNumber > currentTierNumber) {
                // Shift tiers downwards (e.g., if changing from 2 to 5, shift 5 to 4, 4 to 3, 3 to 2)
                shiftTierNumberDownForUpdate(currentTierNumber, newTierNumber);
            } else if (newTierNumber < currentTierNumber) {
                // Shift tiers up (this would apply if moving to a lower tier number)
                shiftTierNumberUpForUpdate(newTierNumber, currentTierNumber);
            }

            existingRoomType.setTierNumber(newTierNumber);
        }

        if (updatedRoomType.getSize() != null && !updatedRoomType.getSize().isEmpty()) {
            existingRoomType.setSize(updatedRoomType.getSize());
        }
        if (updatedRoomType.getBed() != null && !updatedRoomType.getBed().isEmpty()) {
            existingRoomType.setBed(updatedRoomType.getBed());
        }
        if (updatedRoomType.getDescription() != null && !updatedRoomType.getDescription().isEmpty()) {
            existingRoomType.setDescription(updatedRoomType.getDescription());
        }
        if (updatedRoomType.getCapacity() != null) {
            existingRoomType.setCapacity(updatedRoomType.getCapacity());
        }
        if (updatedRoomType.getAmenities() != null && !updatedRoomType.getAmenities().isEmpty()) {
            existingRoomType.setAmenities(updatedRoomType.getAmenities());
        }
        if (updatedRoomType.getRoomTypeStatus() != null) {
            existingRoomType.setRoomTypeStatus(updatedRoomType.getRoomTypeStatus());
        }

        em.merge(existingRoomType);
        em.flush(); // Persist changes

        return existingRoomType;
    }

    //Order by tier number to make it easier to display
    @Override
    public List<RoomType> retrieveAllRoomTypes() {
        Query query = em.createQuery("SELECT rt FROM RoomType rt ORDER BY rt.tierNumber");

        return query.getResultList();
    }

    @Override
    public RoomType retrieveRoomTypeByName(String typeName) throws InvalidRoomTypeNameException {
        try {
            return em.createQuery("SELECT rt FROM RoomType rt WHERE rt.typeName = :typeName", RoomType.class)
                    .setParameter("typeName", typeName)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidRoomTypeNameException("Invalid room type name.");
        }
    }

    @Override
    public RoomType retrieveRoomTypeByTierNumber(Integer tierNumber) throws InvalidRoomTypeTierNumberException {
        try {
            return em.createQuery("SELECT rt FROM RoomType rt WHERE rt.tierNumber = :tierNumber", RoomType.class)
                     .setParameter("tierNumber", tierNumber)
                     .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidRoomTypeTierNumberException("Invalid room type tier number.");
        }
    }
    
    @Override
    public void deleteRoomType(RoomType existingRoomType) throws RoomTypeInUseException {
        //Check if existing room type is linked to any rooms. (Get all rooms linked to that room type)
        //Not sure what they mean by "if room type is not used", but for now assume that it means no rooms are linked
        //But could potentially need to change to check check-in/check-out date?
        RoomType managedRoomType = em.find(RoomType.class, existingRoomType.getRoomTypeId());

        if (roomSessionBeanLocal.retrieveAllRoomsByRoomType(managedRoomType).isEmpty()) {
            em.remove(managedRoomType);
            em.flush();
            //Shift existing tier numbers down too
            shiftTierNumbersDownForDelete(managedRoomType.getTierNumber());
        } else {
            throw new RoomTypeInUseException("Room type is currently in use. Unable to delete.");
        }        
        }

    }

    private void shiftTierNumberUpForCreate(Integer fromTier) {
        List<RoomType> affectedRoomTypes = em.createQuery(
                "SELECT rt FROM RoomType rt WHERE rt.tierNumber >= :fromTier ORDER BY rt.tierNumber ASC", RoomType.class)
                .setParameter("fromTier", fromTier)
                .getResultList();

        for (RoomType roomType : affectedRoomTypes) {
            roomType.setTierNumber(roomType.getTierNumber() + 1);
            em.merge(roomType);
        }
    }

    private void shiftTierNumberUpForUpdate(Integer fromTier, Integer toTier) {
        List<RoomType> affectedRoomTypes = em.createQuery(
                "SELECT rt FROM RoomType rt WHERE rt.tierNumber BETWEEN :fromTier AND :toTier ORDER BY rt.tierNumber ASC", RoomType.class)
                .setParameter("fromTier", fromTier)
                .setParameter("toTier", toTier - 1)
                .getResultList();

        for (RoomType roomType : affectedRoomTypes) {
            roomType.setTierNumber(roomType.getTierNumber() + 1);
            em.merge(roomType);
        }
    }

    private void shiftTierNumberDownForUpdate(Integer fromTier, Integer toTier) {
        List<RoomType> affectedRoomTypes = em.createQuery(
                "SELECT rt FROM RoomType rt WHERE rt.tierNumber BETWEEN :fromTier AND :toTier ORDER BY rt.tierNumber ASC", RoomType.class)
                .setParameter("fromTier", fromTier + 1) // Shift all tiers from the one above the start
                .setParameter("toTier", toTier)
                .getResultList();

        for (RoomType roomType : affectedRoomTypes) {
            roomType.setTierNumber(roomType.getTierNumber() - 1);
            em.merge(roomType);
        }
    }

    private void shiftTierNumbersDownForDelete(Integer fromTier) {
        List<RoomType> affectedRoomTypes = em.createQuery(
                "SELECT rt FROM RoomType rt WHERE rt.tierNumber > :fromTier ORDER BY rt.tierNumber ASC", RoomType.class)
                .setParameter("fromTier", fromTier)
                .getResultList();

        for (RoomType roomType : affectedRoomTypes) {
            roomType.setTierNumber(roomType.getTierNumber() - 1);
            em.merge(roomType);
        }
    }

    private Integer getHighestTierNumber() {
        Query query = em.createQuery("SELECT MAX(rt.tierNumber) FROM RoomType rt");
        Integer maxTierNumber = (Integer) query.getSingleResult();
        return maxTierNumber != null ? maxTierNumber : 0; //In case there are no room types yet.
    }

    @Override
    public BigDecimal getLowestTierDailyRate(Date date, ReservationTypeEnum reservationType, List<Room> rooms) throws InvalidRoomTypeException, InvalidRoomException {
        if (rooms == null || rooms.isEmpty()) {
            throw new InvalidRoomException("No rooms available");
        }

        RoomType lowestTierRoomType = rooms.stream()
                                           .map(Room::getRoomType)
                                           .min((rt1, rt2) -> Integer.compare(rt1.getTierNumber(), rt2.getTierNumber()))
                                           .orElseThrow(() -> new IllegalArgumentException("No valid RoomType found"));

        // Get the daily rate for the lowest-tier RoomType
        return roomRateSessionBeanLocal.getDailyRate(date, lowestTierRoomType, reservationType);
    }

  

}
