/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.RoomSessionBeanLocal;
import entity.Room;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.ejb.Stateless;
import util.exception.InvalidRoomTypeTierNumberException;
import util.exception.UpdateRoomException;

/**
 *
 * @author JorJo
 */
@WebService(serviceName = "RoomWebService")
@Stateless()
public class RoomWebService {

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;    
    
    @WebMethod(operationName = "retrieveAllRooms")
    public List<Room> retrieveAllRooms() {
        return roomSessionBeanLocal.retrieveAllRooms();
    }
    
    @WebMethod(operationName = "retrieveAllAvailableRooms")
    public List<Room> retrieveAllAvailableRooms() {
        return roomSessionBeanLocal.retrieveAllAvailableRooms();
    }
    
    @WebMethod(operationName = "updateRoom")
    public Room updateRoom(Room updatedRoom) throws UpdateRoomException {
        return roomSessionBeanLocal.updateRoom(updatedRoom);
    }
    
    @WebMethod(operationName = "allocateRooms")
    public void allocateRooms() throws InvalidRoomTypeTierNumberException {
        roomSessionBeanLocal.allocateRooms();
    }
    
    
}
