/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author JorJo
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Long createNewReservation(Reservation reservation) {
	em.persist(reservation);
	//If you wish to return the primary key value because our record Id is automatically generated by the DB,
	//need to flush the transaction
	em.flush();
	
	return reservation.getReservationId();
    }
    
    public List<Reservation> retrieveAllReservations() {
	Query query = em.createQuery("SELECT r FROM Reservation r");
	
	return query.getResultList();
		
    }
}
