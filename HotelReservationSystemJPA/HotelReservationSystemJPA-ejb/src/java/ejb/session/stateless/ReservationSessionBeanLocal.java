/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.InvalidReservationIdException;
import util.exception.UpdateRoomException;

/**
 *
 * @author JorJo
 */
@Local
public interface ReservationSessionBeanLocal {

    public Reservation createNewReservation(Reservation newReservation);

    public List<Reservation> retrieveAllReservations();

    public List<RoomRate> retrieveAllReservationsByRoomRate(RoomRate roomRate);

    public boolean isSameDayCheckIn(Date checkInDate, Date currentDate);

    public Reservation getReservationByReservationId(Long reservationId) throws InvalidReservationIdException;
    
    public List<Reservation> retrieveAllReservationsByGuest(Guest guest);

    public Reservation updateReservation(Reservation updatedReservation) throws InvalidReservationIdException;

    public List<Reservation> retrieveAllReservationsByPartner(Partner partner);

    
    
}
