/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.ReservationTypeEnum;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomTypeCapacityException;
import util.exception.InvalidRoomTypeException;
import util.exception.InvalidRoomTypeNameException;
import util.exception.InvalidRoomTypeTierNumberException;
import util.exception.RoomTypeAlreadyExistException;
import util.exception.RoomTypeInUseException;

/**
 *
 * @author JorJo
 */
@Local
public interface RoomTypeSessionBeanLocal {

    public RoomType createNewRoomType(RoomType newRoomType) throws RoomTypeAlreadyExistException, InvalidRoomTypeTierNumberException;

    public List<RoomType> retrieveAllRoomTypes();
    
    public List<RoomType> retrieveAllActiveRoomTypes();

    public RoomType retrieveRoomTypeByName(String typeName) throws InvalidRoomTypeNameException;
    
    public RoomType retrieveActiveRoomTypeByName(String typeName) throws InvalidRoomTypeNameException;

    public RoomType updateRoomType(RoomType updatedRoomType) throws InvalidRoomTypeTierNumberException, InvalidRoomTypeCapacityException;

    public void deleteRoomType(RoomType existingRoomType) throws RoomTypeInUseException;

    public RoomType retrieveRoomTypeByTierNumber(Integer tierNumber) throws InvalidRoomTypeTierNumberException;
    
    public BigDecimal getLowestTierDailyRate(Date date, ReservationTypeEnum reservationType, List<Room> rooms) throws InvalidRoomTypeException, InvalidRoomException;

}
