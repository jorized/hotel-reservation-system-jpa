/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomReservation;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author JorJo
 */
@Remote
public interface RoomReservationSessionBeanRemote {
    
    public RoomReservation createNewRoomReservation(RoomReservation newRoomReservation);
    
    public List<RoomReservation> retrieveRoomReservationsByReservation(Reservation reservation);
    
}
