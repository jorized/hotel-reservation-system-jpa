/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.Reservation;
import entity.Room;
import entity.RoomReservation;
import entity.RoomType;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.ExceptionTypeReportEnum;
import util.exception.InvalidRoomNumException;
import util.exception.RoomAlreadyExistException;
import util.enumeration.RoomStatusEnum;
import util.exception.InvalidRoomTypeTierNumberException;
import util.exception.UpdateRoomException;

/**
 *
 * @author JorJo
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @EJB
    private ExceptionReportSessionBeanLocal exceptionReportSessionBeanLocal;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB
    private RoomReservationSessionBeanLocal roomReservationSessionBeanLocal;                

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;        
    

    @Override
    public Room createNewRoom(Room newRoom) throws RoomAlreadyExistException {

        Room existingRoom = em.createQuery("SELECT r from Room r WHERE r.roomNum = :roomNum", Room.class)
                .setParameter("roomNum", newRoom.getRoomNum())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existingRoom != null) {
            throw new RoomAlreadyExistException("Room '" + newRoom.getRoomNum() + "' already exists.");
        }

        em.persist(newRoom);
        em.flush();

        return newRoom;
    }

    @Override
    public Room retrieveRoomByRoomNum(String roomNum) throws InvalidRoomNumException {
        try {
            return em.createQuery("SELECT rn FROM Room rn WHERE rn.roomNum = :roomNum", Room.class)
                    .setParameter("roomNum", roomNum)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidRoomNumException("Invalid room number");
        }
    }

    @Override
    public Room updateRoom(Room updatedRoom) throws UpdateRoomException {
        try {
            Room existingRoom = em.find(Room.class, updatedRoom.getRoomId());

            if (updatedRoom.getFloorNum() != null) {
                existingRoom.setFloorNum(updatedRoom.getFloorNum());
            }
            if (updatedRoom.getSequenceNum() != null && !updatedRoom.getSequenceNum().isEmpty()) {
                existingRoom.setSequenceNum(updatedRoom.getSequenceNum());
            }
            if (updatedRoom.getRoomNum() != null && !updatedRoom.getRoomNum().isEmpty()) {
                existingRoom.setRoomNum(updatedRoom.getRoomNum());
            }
            if (updatedRoom.getRoomStatus() != null) {
                existingRoom.setRoomStatus(updatedRoom.getRoomStatus());
            }

            em.merge(existingRoom);
            em.flush();

            return existingRoom;
        } catch (Exception e) {
            throw new UpdateRoomException("Error updating room details: " + e.getMessage());
        }

    }

    @Override
    public String deleteRoom(Room existingRoom) {
        // Find the managed room entity
        Room managedRoom = em.find(Room.class, existingRoom.getRoomId());

        // Check if the room has any active reservations or is in use
        if (isRoomInUse(managedRoom.getRoomId())) {
            // If in use, disable the room instead of deleting
            managedRoom.setRoomStatus(RoomStatusEnum.DISABLED);
            em.merge(managedRoom); // Update the status to DISABLED in the database
            em.flush();
            return "Room is in use, status has been changed to 'DISABLED'.\n";
        } else {
            // If not in use, proceed to delete the room
            em.remove(managedRoom);
            em.flush();
            return "Room has successfully been removed.\n";
        }
    }

    public boolean isRoomInUse(Long roomId) {
        // Query to check if there are any active reservations for the room
        return !em.createQuery("SELECT r FROM Room r WHERE r.roomId = :roomId AND r.roomStatus = :notInUseStatus")
                .setParameter("roomId", roomId)
                .setParameter("notInUseStatus", RoomStatusEnum.NOT_AVAILABLE)
                .getResultList()
                .isEmpty();
    }

    @Override
    public List<Room> retrieveAllRoomsByRoomType(RoomType roomType) {
        return em.createQuery("SELECT r FROM Room r WHERE r.roomType = :roomType", Room.class)
                .setParameter("roomType", roomType)
                .getResultList();
    }
    
    @Override
    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r FROM Room r ORDER BY r.roomNum");
	
	return query.getResultList();
    }
    
    @Override
    public List<Room> retrieveAllReservedRoomsByRoomType(RoomType roomType) {
        return em.createQuery("SELECT r FROM Room r WHERE r.roomType = :roomType AND r.roomStatus = :roomStatus", Room.class)
        .setParameter("roomType", roomType)
        .setParameter("roomStatus", RoomStatusEnum.RESERVED)
        .getResultList();
    }
    
    @Override
    public List<Room> retrieveAllAvailableRoomsByRoomType(RoomType roomType) {
        return em.createQuery("SELECT r FROM Room r WHERE r.roomType = :roomType AND r.roomStatus = :roomStatus", Room.class)
        .setParameter("roomType", roomType)
        .setParameter("roomStatus", RoomStatusEnum.AVAILABLE)
        .getResultList();
    }
    
    //Room allocation method (EJB timer method that runs at 2am)
    //@Schedule(hour = "*", minute = "*", second = "*/5", info = "allocateRooms")
    @Schedule(hour = "2", minute = "0", second = "0", info = "allocateRooms")
    public void allocateRooms() throws InvalidRoomTypeTierNumberException {
        System.out.println("*** START ALLOCATING ROOMS ***");
        
        List<Reservation> reservations = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.reservationId NOT IN (SELECT rr.reservation.reservationId FROM RoomReservation rr)",
            Reservation.class
        ).getResultList();


        for (Reservation reservation : reservations) {
            RoomType currentRoomType = reservation.getRoomType();
            int roomsNeeded = reservation.getNumOfRooms();
            int roomsAllocated = 0;

            while (roomsAllocated < roomsNeeded) {
                List<Room> listOfReservedRooms = retrieveAllReservedRoomsByRoomType(currentRoomType);

                if (!listOfReservedRooms.isEmpty()) {
                    // Allocate reserved rooms in current room type
                    Room room = listOfReservedRooms.get(0);
                    RoomReservation newRoomReservation = new RoomReservation(room, reservation);
                    room.setRoomStatus(RoomStatusEnum.ALLOCATED); // Update room status
                    roomReservationSessionBeanLocal.createNewRoomReservation(newRoomReservation);
                    roomsAllocated++;
                } else {
                    // Upgrade to next tier if available
                    RoomType upgradedRoomType = roomTypeSessionBeanLocal.retrieveRoomTypeByTierNumber(currentRoomType.getTierNumber() + 1);

                    //Need to check by AVAILABLE instead of RESERVED now
                    if (upgradedRoomType != null) {
                        List<Room> upgradedAvailRooms = retrieveAllAvailableRoomsByRoomType(upgradedRoomType);

                        if (!upgradedAvailRooms.isEmpty()) {
                            Room upgradedRoom = upgradedAvailRooms.get(0);
                            RoomReservation newRoomReservation = new RoomReservation(upgradedRoom, reservation);
                            upgradedRoom.setRoomStatus(RoomStatusEnum.ALLOCATED);
                            roomReservationSessionBeanLocal.createNewRoomReservation(newRoomReservation);
                            roomsAllocated++;
                            //Upgraded from reservation.getRoomType() to ExceptionReport.getRoomReservation().getRoom().getRoomType()
                            ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_1, newRoomReservation);
                            exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);
                        } else {

                            RoomReservation emptyRoomReservation = new RoomReservation(null, reservation);
                            roomReservationSessionBeanLocal.createNewRoomReservation(emptyRoomReservation);

                            ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                            exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);
                            break;
                        }
                    } else {

                        RoomReservation emptyRoomReservation = new RoomReservation(null, reservation);
                        roomReservationSessionBeanLocal.createNewRoomReservation(emptyRoomReservation);

                        ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                        exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);
                        break; 
                    }
                }
            }
        }
        System.out.println("*** DONE ALLOCATING ROOMS ***");
    }

    @Override
    public List<Room> retrieveAllAvailableRooms() {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomStatus = :availableStatus", Room.class)
                        .setParameter("availableStatus", RoomStatusEnum.AVAILABLE);

        List<Room> rooms = query.getResultList();
        System.out.println("Rooms retrieved: " + rooms);
        return rooms;
    }
    
    @Override
    public boolean checkRoomNum(String roomNum) {
        return em.createQuery("SELECT COUNT(r) FROM Room r WHERE r.roomNum = :roomNum", Long.class)
                 .setParameter("roomNum", roomNum)
                 .getSingleResult() > 0;
    }
    
    
    
}
