/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomType;
import javax.ejb.Local;

/**
 *
 * @author JorJo
 */
@Local
public interface RoomTypeSessionBeanLocal {

    public RoomType createNewRoomType(RoomType newRoomType);
    
}
