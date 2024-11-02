/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.enumeration.ReservationTypeEnum;
import util.exception.InvalidRoomRateException;
import util.exception.InvalidRoomRateNameException;
import util.exception.ReservationInUseException;
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

    public void deleteRoomRate(RoomRate existingRoomRate) throws ReservationInUseException;

    public List<RoomRate> retrieveAllRoomRates();

    public BigDecimal getDailyRate(Date date, RoomType roomType, ReservationTypeEnum reservationTypeEnum);

    public RoomRate getRoomRateForType(RoomType roomType, Date currentDate) throws InvalidRoomRateException;
    
}
