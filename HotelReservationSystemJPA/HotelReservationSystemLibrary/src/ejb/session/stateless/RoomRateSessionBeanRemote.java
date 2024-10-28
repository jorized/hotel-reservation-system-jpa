/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import javax.ejb.Remote;
import util.exception.InvalidRoomRateNameException;
import util.exception.RoomRateAlreadyExistException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author JorJo
 */
@Remote
public interface RoomRateSessionBeanRemote {

    public RoomRate createNewRoomRate(RoomRate newRoomRate) throws RoomRateAlreadyExistException;

    public RoomRate retrieveRoomRateByRoomName(String rateName) throws InvalidRoomRateNameException;

    public RoomRate updateRoomRate(RoomRate updatedRoomRate) throws UpdateRoomRateException;
}
