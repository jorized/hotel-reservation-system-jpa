/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.RoomAlreadyExistException;

/**
 *
 * @author JorJo
 */
@Local
public interface RoomSessionBeanLocal {

    public Room createNewRoom(Room newRoom) throws RoomAlreadyExistException;

    public List<Room> retrieveAllRoomsByRoomType(RoomType roomType);
    
}
