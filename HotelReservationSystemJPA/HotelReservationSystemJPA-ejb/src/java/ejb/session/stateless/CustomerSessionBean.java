/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.AccountAlreadyExistException;
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

        Customer existingCustomer = em.createQuery("SELECT c FROM Customer c WHERE c.username = :username", Customer.class)
                          .setParameter("username", newCustomer.getUsername())
                          .getResultStream()
                          .findFirst()
                          .orElse(null);                
        
        if (existingCustomer != null) {
            throw new AccountAlreadyExistException("This username '" + newCustomer.getUsername() + "' already exists.");
        }
        
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
    
    
    
    

    
}
