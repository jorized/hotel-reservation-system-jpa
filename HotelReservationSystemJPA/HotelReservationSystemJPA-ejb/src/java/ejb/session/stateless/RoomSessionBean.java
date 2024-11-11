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
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
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
    
    @Resource
    private TimerService timerService;

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
            "SELECT r FROM Reservation r WHERE r.reservationId NOT IN (SELECT rr.reservation.reservationId FROM RoomReservation rr) AND r.checkInDate = CURRENT_DATE",
            Reservation.class
        ).getResultList();

        for (Reservation reservation : reservations) {
            RoomType currentRoomType = reservation.getRoomType();
            int roomsNeeded = reservation.getNumOfRooms();
            int roomsAllocated = 0;

            System.out.println("Processing Reservation ID: " + reservation.getReservationId());
            System.out.println("Current Room Type: " + currentRoomType.getTypeName());
            System.out.println("Rooms Needed: " + roomsNeeded);

            while (roomsAllocated < roomsNeeded) {
                List<Room> reservedRooms = retrieveAllReservedRoomsByRoomType(currentRoomType);

                if (!reservedRooms.isEmpty()) {
                    Room room = reservedRooms.get(0);
                    System.out.println("Allocating Reserved Room: " + room.getRoomNum());
                    RoomReservation newRoomReservation = new RoomReservation(room, reservation);
                    room.setRoomStatus(RoomStatusEnum.ALLOCATED);
                    roomReservationSessionBeanLocal.createNewRoomReservation(newRoomReservation);
                    roomsAllocated++;
                } else {
                    System.out.println("No Reserved Rooms Available in Current Room Type.");
                    // Upgrade to next tier if available
                    try {
                        RoomType upgradedRoomType = roomTypeSessionBeanLocal.retrieveRoomTypeByTierNumber(currentRoomType.getTierNumber() + 1);
                        if (upgradedRoomType != null) {
                            List<Room> upgradedAvailableRooms = retrieveAllAvailableRoomsByRoomType(upgradedRoomType);
                            if (!upgradedAvailableRooms.isEmpty()) {
                                Room upgradedRoom = upgradedAvailableRooms.get(0);
                                System.out.println("Allocating Available Upgraded Room: " + upgradedRoom.getRoomNum());
                                RoomReservation newRoomReservation = new RoomReservation(upgradedRoom, reservation);
                                upgradedRoom.setRoomStatus(RoomStatusEnum.ALLOCATED);
                                roomReservationSessionBeanLocal.createNewRoomReservation(newRoomReservation);
                                roomsAllocated++;
                                // Create Exception Report
                                ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_1, newRoomReservation);
                                exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);
                            } else {
                                System.out.println("No Available Rooms in Upgraded Room Type.");
                                // Create Exception Report Type 2
                                RoomReservation emptyRoomReservation = new RoomReservation(null, reservation);
                                roomReservationSessionBeanLocal.createNewRoomReservation(emptyRoomReservation);

                                ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                                exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);

                                roomsAllocated++;
                            }
                        } else {
                            System.out.println("No Higher Tier Room Type Available for Upgrade.");
                            // Create Exception Report Type 2
                            RoomReservation emptyRoomReservation = new RoomReservation(null, reservation);
                            roomReservationSessionBeanLocal.createNewRoomReservation(emptyRoomReservation);

                            ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                            exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);

                            roomsAllocated++;
                        }
                    } catch (InvalidRoomTypeTierNumberException ex) {
                        System.out.println("No Higher Tier Room Type Available for Upgrade.");
                        // Create Exception Report Type 2
                        RoomReservation emptyRoomReservation = new RoomReservation(null, reservation);
                        roomReservationSessionBeanLocal.createNewRoomReservation(emptyRoomReservation);

                        ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                        exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);

                        roomsAllocated++;
                    }                    
                }
            }
        }
        System.out.println("*** DONE ALLOCATING ROOMS ***");
    }     
    
    @Override
    public void allocateRoomsManually(Date checkInDate) throws InvalidRoomTypeTierNumberException {
        System.out.println("*** START MANUALLY ALLOCATING ROOMS ***");

        List<Reservation> reservations = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.reservationId NOT IN (SELECT rr.reservation.reservationId FROM RoomReservation rr) AND r.checkInDate = :checkInDate",
            Reservation.class
        )
        .setParameter("checkInDate", checkInDate)
        .getResultList();

        for (Reservation reservation : reservations) {
            RoomType currentRoomType = reservation.getRoomType();
            int roomsNeeded = reservation.getNumOfRooms();
            int roomsAllocated = 0;

            System.out.println("Processing Reservation ID: " + reservation.getReservationId());
            System.out.println("Current Room Type: " + currentRoomType.getTypeName());
            System.out.println("Rooms Needed: " + roomsNeeded);

            while (roomsAllocated < roomsNeeded) {
                List<Room> reservedRooms = retrieveAllReservedRoomsByRoomType(currentRoomType);

                if (!reservedRooms.isEmpty()) {
                    Room room = reservedRooms.get(0);
                    System.out.println("Allocating Reserved Room: " + room.getRoomNum());
                    RoomReservation newRoomReservation = new RoomReservation(room, reservation);
                    room.setRoomStatus(RoomStatusEnum.ALLOCATED);
                    roomReservationSessionBeanLocal.createNewRoomReservation(newRoomReservation);
                    roomsAllocated++;
                } else {
                    System.out.println("No Reserved Rooms Available in Current Room Type.");
                    // Upgrade to next tier if available
                    try {
                        RoomType upgradedRoomType = roomTypeSessionBeanLocal.retrieveRoomTypeByTierNumber(currentRoomType.getTierNumber() + 1);
                        if (upgradedRoomType != null) {
                            List<Room> upgradedAvailableRooms = retrieveAllAvailableRoomsByRoomType(upgradedRoomType);
                            if (!upgradedAvailableRooms.isEmpty()) {
                                Room upgradedRoom = upgradedAvailableRooms.get(0);
                                System.out.println("Allocating Available Upgraded Room: " + upgradedRoom.getRoomNum());
                                RoomReservation newRoomReservation = new RoomReservation(upgradedRoom, reservation);
                                upgradedRoom.setRoomStatus(RoomStatusEnum.ALLOCATED);
                                roomReservationSessionBeanLocal.createNewRoomReservation(newRoomReservation);
                                roomsAllocated++;
                                // Create Exception Report
                                ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_1, newRoomReservation);
                                exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);
                            } else {
                                System.out.println("No Available Rooms in Upgraded Room Type.");
                                // Create Exception Report Type 2
                                RoomReservation emptyRoomReservation = new RoomReservation(null, reservation);
                                roomReservationSessionBeanLocal.createNewRoomReservation(emptyRoomReservation);

                                ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                                exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);

                                roomsAllocated++;
                            }
                        } else {
                            System.out.println("No Higher Tier Room Type Available for Upgrade.");
                            // Create Exception Report Type 2
                            RoomReservation emptyRoomReservation = new RoomReservation(null, reservation);
                            roomReservationSessionBeanLocal.createNewRoomReservation(emptyRoomReservation);

                            ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                            exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);

                            roomsAllocated++;
                        }
                    } catch (InvalidRoomTypeTierNumberException ex) {
                        System.out.println("No Higher Tier Room Type Available for Upgrade.");
                        // Create Exception Report Type 2
                        RoomReservation emptyRoomReservation = new RoomReservation(null, reservation);
                        roomReservationSessionBeanLocal.createNewRoomReservation(emptyRoomReservation);

                        ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                        exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);

                        roomsAllocated++;
                    }                    
                }
            }
        }
        System.out.println("*** DONE MANUALY ALLOCATING ROOMS ***");
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
    
    @Override
    public Room retrieveRoomByRoomId(Long roomId) {
        Room room = em.createQuery("SELECT r FROM Room r WHERE r.roomId = :roomId", Room.class)
                 .setParameter("roomId", roomId)
                 .getSingleResult();
        em.refresh(room);
        return room;
    }   
    
    
}
