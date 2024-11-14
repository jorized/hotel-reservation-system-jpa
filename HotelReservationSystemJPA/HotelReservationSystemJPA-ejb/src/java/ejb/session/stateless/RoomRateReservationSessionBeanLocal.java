/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomRateReservation;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author JorJo
 */
@Local
public interface RoomRateReservationSessionBeanLocal {

    public RoomRateReservation createNewRoomRateReservation(RoomRateReservation newRoomRateReservation);

    public List<RoomRateReservation> retrieveRoomRateReservationsByRoomRate(RoomRate roomRate);
    
}
