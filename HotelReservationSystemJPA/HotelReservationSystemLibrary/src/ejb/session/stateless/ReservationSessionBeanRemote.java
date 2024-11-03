/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.UpdateRoomException;

/**
 *
 * @author JorJo
 */
@Remote
public interface ReservationSessionBeanRemote {

    public Reservation createNewReservation(Reservation newReservation);

    public List<Reservation> retrieveAllReservations();

    public List<RoomRate> retrieveAllReservationsByRoomRate(RoomRate roomRate);

    public boolean isSameDayCheckIn(Date checkInDate, Date currentDate);

    public void allocateRoomImmediately(Reservation reservation, int noOfRooms, List<Room> availableRooms) throws UpdateRoomException;
    
    public Reservation getReservationByReservationId(Long reservationId);
    
    public List<Reservation> retrieveAllReservationsByGuest(Guest guest);
}
