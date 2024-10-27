/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.InvalidRoomTypeCapacityException;
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

    public RoomType retrieveRoomTypeByName(String typeName) throws InvalidRoomTypeNameException;

    public RoomType updateRoomType(RoomType updatedRoomType) throws InvalidRoomTypeTierNumberException, InvalidRoomTypeCapacityException;

    public void deleteRoomType(RoomType existingRoomType) throws RoomTypeInUseException;
    
}
