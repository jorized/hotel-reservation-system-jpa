/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.InvalidGuestEmailException;
import util.exception.InvalidGuestPassportNumberException;
import util.exception.InvalidGuestPhoneNumberException;

/**
 *
 * @author JorJo
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    @Override
    public Guest createNewGuest(Guest newGuest) {
	em.persist(newGuest);

	em.flush();
	
	return newGuest;
    }
    
    @Override
    public Guest retrieveGuestByEmail(String email) throws InvalidGuestEmailException {
        try {
            return em.createQuery("SELECT g FROM Guest g WHERE g.email = :email", Guest.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidGuestEmailException("Invalid customer email.");
        }
    }
    
    @Override
    public Guest retrieveGuestByPhoneNumber(String phoneNumber) throws InvalidGuestPhoneNumberException {
        try {
            return em.createQuery("SELECT g FROM Guest g WHERE g.phoneNumber = :phoneNumber", Guest.class)
                    .setParameter("phoneNumber", phoneNumber)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidGuestPhoneNumberException("Invalid customer phone number.");
        }
    }
    
    @Override
    public Guest retrieveGuestByPassportNumber(String passportNumber) throws InvalidGuestPassportNumberException {
        try {
            return em.createQuery("SELECT g FROM Guest g WHERE g.passportNumber = :passportNumber", Guest.class)
                    .setParameter("passportNumber", passportNumber)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidGuestPassportNumberException("Invalid customer passport number.");
        }
    }
}
