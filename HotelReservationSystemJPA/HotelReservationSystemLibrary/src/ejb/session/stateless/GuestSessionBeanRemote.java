/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Remote;

/**
 *
 * @author JorJo
 */
@Remote
public interface GuestSessionBeanRemote {
    
    public Guest createNewGuest(Guest newGuest);
}
