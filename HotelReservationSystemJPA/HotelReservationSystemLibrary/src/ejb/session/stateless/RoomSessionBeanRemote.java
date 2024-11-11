/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InvalidRoomNumException;
import util.exception.InvalidRoomTypeTierNumberException;
import util.exception.RoomAlreadyExistException;
import util.exception.RoomInUseException;
import util.exception.UpdateRoomException;

/**
 *
 * @author JorJo
 */
@Remote
public interface RoomSessionBeanRemote {
    
    public Room createNewRoom(Room newRoom) throws RoomAlreadyExistException;
    
    public List<Room> retrieveAllRoomsByRoomType(RoomType roomType);
    
    public Room retrieveRoomByRoomNum(String roomNum) throws InvalidRoomNumException;
    
    public Room updateRoom(Room updatedRoom) throws UpdateRoomException;
    
    public String deleteRoom(Room existingRoom);
    
    public List<Room> retrieveAllRooms();
    
    public List<Room> retrieveAllReservedRoomsByRoomType(RoomType roomType);
    
    public List<Room> retrieveAllAvailableRooms();

    public boolean checkRoomNum(String roomNum);
    
    public void allocateRooms() throws InvalidRoomTypeTierNumberException;
    
    public void allocateRoomsManually(Date checkInDate) throws InvalidRoomTypeTierNumberException;
    
    public List<Room> retrieveAllAvailableRoomsByRoomType(RoomType roomType);

    public Room retrieveRoomByRoomId(Long roomId);

}
