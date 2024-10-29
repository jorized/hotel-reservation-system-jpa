/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import java.util.List;
import javax.ejb.Local;
import util.exception.InvalidRoomRateNameException;
import util.exception.ReservationInUseException;
import util.exception.RoomRateAlreadyExistException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author JorJo
 */
@Local
public interface RoomRateSessionBeanLocal {

    public RoomRate createNewRoomRate(RoomRate newRoomRate) throws RoomRateAlreadyExistException;

    public RoomRate retrieveRoomRateByRoomName(String rateName) throws InvalidRoomRateNameException;

    public RoomRate updateRoomRate(RoomRate updatedRoomRate) throws UpdateRoomRateException;

    public void deleteRoomRate(RoomRate existingRoomRate) throws ReservationInUseException;

    public List<RoomRate> retrieveAllRoomRates();
    
}
