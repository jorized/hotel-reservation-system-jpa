/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author JorJo
 */
@Local
public interface ReservationSessionBeanLocal {

    public Long createNewReservation(Reservation reservation);

    public List<Reservation> retrieveAllReservations();
    
}
