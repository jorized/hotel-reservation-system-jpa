/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Remote;
import util.exception.InvalidGuestEmailException;
import util.exception.InvalidGuestPassportNumberException;
import util.exception.InvalidGuestPhoneNumberException;

/**
 *
 * @author JorJo
 */
@Remote
public interface GuestSessionBeanRemote {
    
    public Guest createNewGuest(Guest newGuest);
    
    public Guest retrieveGuestByEmail(String email) throws InvalidGuestEmailException;
    
    public Guest retrieveGuestByPhoneNumber(String phoneNumber) throws InvalidGuestPhoneNumberException;
    
    public Guest retrieveGuestByPassportNumber(String passportNumber) throws InvalidGuestPassportNumberException;
}
