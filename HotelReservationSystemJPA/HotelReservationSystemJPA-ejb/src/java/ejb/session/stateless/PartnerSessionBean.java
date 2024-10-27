/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import entity.Partner;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AccountAlreadyExistException;
import util.exception.InvalidLoginCredentialsException;

/**
 *
 * @author JorJo
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Partner createNewPartner(Partner newPartner) throws AccountAlreadyExistException {        

        Partner existingPartner = em.createQuery("SELECT p FROM Partner p WHERE p.username = :username", Partner.class)
                          .setParameter("username", newPartner.getUsername())
                          .getResultStream()
                          .findFirst()
                          .orElse(null);                
        
        if (existingPartner != null) {
            throw new AccountAlreadyExistException("Partner with username '" + newPartner.getUsername() + "' already exists.");
        }
        
	em.persist(newPartner);
	em.flush();
	
	return newPartner;
    }
    
    @Override
    public List<Partner> retrieveAllPartners() {
        Query query = em.createQuery("SELECT p FROM Partner p");
	
	return query.getResultList();
    }
}
