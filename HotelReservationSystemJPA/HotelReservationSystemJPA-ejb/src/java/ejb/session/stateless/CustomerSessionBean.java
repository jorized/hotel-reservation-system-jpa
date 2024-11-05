/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Guest;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.AccountAlreadyExistException;
import util.exception.InvalidAccountCredentialsException;
import util.exception.InvalidLoginCredentialsException;

/**
 *
 * @author JorJo
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    @Override
    public Customer createNewCustomer(Customer newCustomer) throws AccountAlreadyExistException {        

        // Check for duplicate username if it is not null
        if (newCustomer.getUsername() != null) {
            Customer existingCustomerByUsername = em.createQuery("SELECT c FROM Customer c WHERE c.username = :username", Customer.class)
                                                   .setParameter("username", newCustomer.getUsername())
                                                   .getResultStream()
                                                   .findFirst()
                                                   .orElse(null);                
            if (existingCustomerByUsername != null) {
                throw new AccountAlreadyExistException("This username '" + newCustomer.getUsername() + "' already exists.");
            }
        }

        // Check for duplicate email if it is not null
        if (newCustomer.getEmail() != null) {
            Customer existingCustomerByEmail = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                                                 .setParameter("email", newCustomer.getEmail())
                                                 .getResultStream()
                                                 .findFirst()
                                                 .orElse(null);
            if (existingCustomerByEmail != null) {
                throw new AccountAlreadyExistException("This email '" + newCustomer.getEmail() + "' already exists.");
            }
        }

        // Check for duplicate passport number if it is not null
        if (newCustomer.getPassportNumber() != null) {
            Customer existingCustomerByPassportNumber = em.createQuery("SELECT c FROM Customer c WHERE c.passportNumber = :passportNumber", Customer.class)
                                                          .setParameter("passportNumber", newCustomer.getPassportNumber())
                                                          .getResultStream()
                                                          .findFirst()
                                                          .orElse(null);
            if (existingCustomerByPassportNumber != null) {
                throw new AccountAlreadyExistException("This passport number '" + newCustomer.getPassportNumber() + "' already exists.");
            }
        }

        // Check for duplicate phone number if it is not null
        if (newCustomer.getPhoneNumber() != null) {
            Customer existingCustomerByPhoneNumber = em.createQuery("SELECT c FROM Customer c WHERE c.phoneNumber = :phoneNumber", Customer.class)
                                                       .setParameter("phoneNumber", newCustomer.getPhoneNumber())
                                                       .getResultStream()
                                                       .findFirst()
                                                       .orElse(null);
            if (existingCustomerByPhoneNumber != null) {
                throw new AccountAlreadyExistException("This phone number '" + newCustomer.getPhoneNumber() + "' already exists.");
            }
        }

        // If no duplicates are found, persist the new customer
        em.persist(newCustomer);
        em.flush();

        return newCustomer;
    }


    
    @Override
    public Customer customerLogin(String username, String password) throws InvalidLoginCredentialsException {
        try {
            return em.createQuery("SELECT c FROM Customer c WHERE c.username = :username AND c.password = :password", Customer.class)
                     .setParameter("username", username)
                     .setParameter("password", password)
                     .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidLoginCredentialsException("Invalid login credentials.");
        }
    }
    
    @Override
    public Customer retrieveCustomerByGuest(Guest guest) throws InvalidAccountCredentialsException {
        try {
            return em.createQuery("SELECT c FROM Customer c WHERE c.guestId = :guestId", Customer.class)
                     .setParameter("guestId", guest.getGuestId())
                     .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidAccountCredentialsException("Invalid account credentials");
        }
    }
    
    
    
    

    
}
