/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.RoomAlreadyExistException;

/**
 *
 * @author JorJo
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    @Override
    public Room createNewRoom(Room newRoom) throws RoomAlreadyExistException {
     
        Room existingRoom = em.createQuery("SELECT r from Room r WHERE r.roomNum = :roomNum", Room.class)
                 .setParameter("roomNum", newRoom.getRoomNum())
                 .getResultStream()
                 .findFirst()
                 .orElse(null);
        
        if (existingRoom != null) {
            throw new RoomAlreadyExistException("Room '" + newRoom.getRoomNum() + "' already exists.");
        }
        
	em.persist(newRoom);
	em.flush();
	
	return newRoom;
    }
    
    public Room retrieveRoomByRoomNum(String roomNumString) {
        try {
            
        } catch (NoResultException ex) {
            throw new InvalidRoomNumException("Invalid room number");
        }
    }
    
    @Override
    public List<Room> retrieveAllRoomsByRoomType(RoomType roomType) {
        return em.createQuery("SELECT r FROM Room r WHERE r.roomType = :roomType", Room.class)
                 .setParameter("roomType", roomType)
                 .getResultList();
    }
}
