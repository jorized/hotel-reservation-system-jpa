/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomRate;
import java.util.List;
import javax.ejb.Local;
import util.exception.ReservationInUseException;

/**
 *
 * @author JorJo
 */
@Local
public interface ReservationSessionBeanLocal {

    public Reservation createNewReservation(Reservation newReservation);

    public List<Reservation> retrieveAllReservations();
    
    public List<RoomRate> retrieveAllReservationsByRoomRate(RoomRate roomRate);
    
}
