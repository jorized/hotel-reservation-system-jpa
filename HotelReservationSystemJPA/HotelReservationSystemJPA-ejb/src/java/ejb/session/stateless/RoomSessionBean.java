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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import util.exception.InvalidExceptionReportException;
import util.exception.InvalidRoomReservationException;
import util.exception.InvalidRoomTypeTierNumberException;
import util.exception.UpdateRoomException;
import util.exception.UpdateRoomReservationException;

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
        if (managedRoom.getRoomStatus() == RoomStatusEnum.AVAILABLE) {
            // If AVAILABLE, proceed to delete the room
            em.remove(managedRoom);
            em.flush();
            return "Room has successfully been removed.\n";
        } else {
            // If not AVAILABLE, mark the room as DISABLED instead
            managedRoom.setRoomStatus(RoomStatusEnum.DISABLED);
            em.merge(managedRoom); // Update the status to DISABLED in the database
            em.flush();
            return "Room is not available, room status has been changed to 'DISABLED'.\n";
        }
    }

    @Override
    public List<Room> retrieveAllRoomsByRoomType(RoomType roomType) {
        return em.createQuery("SELECT r FROM Room r WHERE r.roomType = :roomType", Room.class)
                .setParameter("roomType", roomType)
                .getResultList();
    }

    @Override
    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r FROM Room r ORDER BY r.roomType.tierNumber");

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

    //Room allocation method (EJB timer method that runs daily at 2am)
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
                    // New check: attempt to find available rooms in the current room type
                    List<Room> availableRooms = retrieveAllAvailableRoomsByRoomType(currentRoomType);
                    if (!availableRooms.isEmpty()) {
                        Room room = availableRooms.get(0);
                        System.out.println("Allocating Available Room in Current Room Type: " + room.getRoomNum());
                        RoomReservation newRoomReservation = new RoomReservation(room, reservation);
                        room.setRoomStatus(RoomStatusEnum.ALLOCATED);
                        roomReservationSessionBeanLocal.createNewRoomReservation(newRoomReservation);
                        roomsAllocated++;
                    } else {
                        System.out.println("No Available Rooms in Current Room Type, Attempting Upgrade.");
                        // Upgrade to the next tier if available
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
                            System.out.println("Error: " + ex.getMessage());
                        }
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
                    // New check: attempt to find available rooms in the current room type
                    List<Room> availableRooms = retrieveAllAvailableRoomsByRoomType(currentRoomType);
                    if (!availableRooms.isEmpty()) {
                        Room room = availableRooms.get(0);
                        System.out.println("Allocating Available Room in Current Room Type: " + room.getRoomNum());
                        RoomReservation newRoomReservation = new RoomReservation(room, reservation);
                        room.setRoomStatus(RoomStatusEnum.ALLOCATED);
                        roomReservationSessionBeanLocal.createNewRoomReservation(newRoomReservation);
                        roomsAllocated++;
                    } else {
                        System.out.println("No Available Rooms in Current Room Type, Attempting Upgrade.");
                        // Upgrade to the next tier if available
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
                            System.out.println("Error: " + ex.getMessage());
                            // Create Exception Report Type 2 for the error case
                            RoomReservation emptyRoomReservation = new RoomReservation(null, reservation);
                            roomReservationSessionBeanLocal.createNewRoomReservation(emptyRoomReservation);

                            ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                            exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);

                            roomsAllocated++;
                        }
                    }
                }
            }
        }
        System.out.println("*** DONE MANUALLY ALLOCATING ROOMS ***");
    }

    
    //For check-in guest
    @Override
    public List<String> allocateAvailableRoomsOrUpgrade(Reservation reservation) throws InvalidExceptionReportException, InvalidRoomTypeTierNumberException, UpdateRoomException, UpdateRoomReservationException, InvalidRoomReservationException {
        List<RoomReservation> roomReservations = roomReservationSessionBeanLocal.retrieveRoomReservationsByReservation(reservation);
        List<String> allocatedRooms = new ArrayList<>();

        if (roomReservations.isEmpty()) {
            throw new InvalidRoomReservationException("No rooms have yet to be allocated for this reservation.");
        }

        int requiredRooms = reservation.getNumOfRooms();
        int availableOrUpgradableRoomsCount = 0;
        Set<Room> checkedRooms = new HashSet<>();

        // Step 1: Pre-check for sufficient available or upgradable rooms (without making allocations)
        for (RoomReservation roomReservation : roomReservations) {
            Room room = roomReservation.getRoom();

            // Check if room is null
            if (room == null) {
                // Handle the null room case - e.g., log a message, skip, or create an exception report.
                System.out.println("RoomReservation has no assigned room. Skipping or handling accordingly.");
                continue; 
            }

            // Proceed as usual if room is not null
            if (room.getRoomStatus() == RoomStatusEnum.ALLOCATED && checkedRooms.add(room)) {
                availableOrUpgradableRoomsCount++;
                continue;
            }

            RoomType currentRoomType = room.getRoomType();

            List<Room> availableRooms = retrieveAllAvailableRoomsByRoomType(currentRoomType);
            boolean roomFound = false;

            for (Room availableRoom : availableRooms) {
                if (checkedRooms.add(availableRoom)) {
                    availableOrUpgradableRoomsCount++;
                    roomFound = true;
                    break;
                }
            }

            if (roomFound) {
                continue;
            }

            try {
                RoomType upgradedRoomType = roomTypeSessionBeanLocal.retrieveRoomTypeByTierNumber(currentRoomType.getTierNumber() + 1);
                if (upgradedRoomType != null) {
                    List<Room> upgradedAvailableRooms = retrieveAllAvailableRoomsByRoomType(upgradedRoomType);
                    for (Room upgradedRoom : upgradedAvailableRooms) {
                        if (checkedRooms.add(upgradedRoom)) {
                            availableOrUpgradableRoomsCount++;
                            roomFound = true;
                            break;
                        }
                    }
                }
            } catch (InvalidRoomTypeTierNumberException ex) {
                System.out.println("No Higher Tier Room Type Available for Upgrade.");
            }

            // If no room or upgrade option is available, create a Type 2 Exception Report if it doesn't already exist
            if (!roomFound) {
                System.out.println("No Higher Tier Room Type Available for Upgrade.");

                // Attempt to retrieve an existing RoomReservation for this reservation with a null room
                RoomReservation emptyRoomReservation = roomReservationSessionBeanLocal.retrieveRoomReservationByRoomAndReservation(null, reservation);

                if (emptyRoomReservation == null) {

                    // Create a new RoomReservation if none exists
                    RoomReservation newemptyRoomReservation = new RoomReservation(null, reservation);
                    roomReservationSessionBeanLocal.createNewRoomReservation(newemptyRoomReservation);

                    // Use the new RoomReservation for the Exception Report
                    boolean type2ReportExists = checkType2ReportExists(newemptyRoomReservation);
                    if (!type2ReportExists) {
                        ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, newemptyRoomReservation);
                        exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);
                    }
                } else {
                    // If an existing RoomReservation was found, use it
                    boolean type2ReportExists = checkType2ReportExists(emptyRoomReservation);
                    if (!type2ReportExists) {
                        ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_2, emptyRoomReservation);
                        exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);
                    }
                }
            }

        }

        if (availableOrUpgradableRoomsCount < requiredRooms) {
            return null; 
        }

        // Step 2: Only perform the actual allocation and upgrades if the pre-check confirmed sufficient rooms
        int allocatedRoomsCount = 0;
        for (RoomReservation roomReservation : roomReservations) {

            Room room = roomReservation.getRoom();

            if (room != null && room.getRoomStatus() == RoomStatusEnum.ALLOCATED) {
                room.setRoomStatus(RoomStatusEnum.OCCUPIED);
                updateRoom(room);
                roomReservationSessionBeanLocal.updateRoomReservation(roomReservation);
                allocatedRooms.add(room.getRoomNum());
                allocatedRoomsCount++;
                continue;
            }

            RoomType currentRoomType = roomReservation.getRoom().getRoomType();
            List<Room> availableRooms = retrieveAllAvailableRoomsByRoomType(currentRoomType);

            if (!availableRooms.isEmpty()) {
                Room newRoom = availableRooms.get(0);
                roomReservation.setRoom(newRoom);
                newRoom.setRoomStatus(RoomStatusEnum.OCCUPIED);
                updateRoom(newRoom);
                roomReservationSessionBeanLocal.updateRoomReservation(roomReservation);
                allocatedRooms.add(newRoom.getRoomNum());
                allocatedRoomsCount++;
                continue;
            }

            RoomType upgradedRoomType = roomTypeSessionBeanLocal.retrieveRoomTypeByTierNumber(currentRoomType.getTierNumber() + 1);
            if (upgradedRoomType != null) {
                List<Room> upgradedAvailableRooms = retrieveAllAvailableRoomsByRoomType(upgradedRoomType);
                if (!upgradedAvailableRooms.isEmpty()) {
                    Room upgradedRoom = upgradedAvailableRooms.get(0);
                    roomReservation.setRoom(upgradedRoom);
                    upgradedRoom.setRoomStatus(RoomStatusEnum.OCCUPIED);
                    updateRoom(upgradedRoom);
                    roomReservationSessionBeanLocal.updateRoomReservation(roomReservation);

                    ExceptionReport exceptionReport = new ExceptionReport(ExceptionTypeReportEnum.TYPE_1, roomReservation);
                    exceptionReportSessionBeanLocal.createNewExceptionReport(exceptionReport);

                    allocatedRooms.add(upgradedRoom.getRoomNum() + " (Upgraded)");
                    allocatedRoomsCount++;
                    continue;
                }
            }
        }

        return allocatedRoomsCount >= requiredRooms ? allocatedRooms : null;
    }

    private boolean checkType2ReportExists(RoomReservation roomReservation) throws InvalidExceptionReportException {
        ExceptionReport report = exceptionReportSessionBeanLocal.retrieveExceptionReportByRoomReservation(roomReservation);
        System.out.println("report: " + report);
        return report != null && report.getExceptionTypeReport()== ExceptionTypeReportEnum.TYPE_2;
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
    public List<Room> retrieveAllAvailableAndPredictedRooms(Date checkInDate, Date checkOutDate) {
        Query query = em.createQuery(
            "SELECT r FROM Room r " +
            "JOIN r.roomType rt " +
            "WHERE (r.roomStatus = :availableStatus " +
            "       AND NOT EXISTS (SELECT res FROM Reservation res " +
            "                       JOIN res.roomType rt2 " +
            "                       WHERE res.checkInDate < :checkOutDate " +
            "                         AND res.checkOutDate > :checkInDate " +
            "                         AND rt2 = rt)) " +
            "   OR ((r.roomStatus = :reservedStatus OR r.roomStatus = :allocatedStatus OR r.roomStatus = :occupiedStatus) " +
            "       AND NOT EXISTS (SELECT res FROM Reservation res " +
            "                       JOIN res.roomType rt3 " +
            "                       WHERE res.checkInDate < :checkOutDate " +
            "                         AND res.checkOutDate > :checkInDate " +
            "                         AND rt3 = rt))",
            Room.class
        );
        query.setParameter("availableStatus", RoomStatusEnum.AVAILABLE);
        query.setParameter("reservedStatus", RoomStatusEnum.RESERVED);
        query.setParameter("allocatedStatus", RoomStatusEnum.ALLOCATED);
        query.setParameter("occupiedStatus", RoomStatusEnum.OCCUPIED);
        query.setParameter("checkInDate", checkInDate);
        query.setParameter("checkOutDate", checkOutDate);

        return query.getResultList();
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
