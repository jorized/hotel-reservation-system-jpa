/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Reservation;
import java.time.LocalDate;
import java.time.Month;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author JorJo
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void postConstruct() {
        
        LocalDate startDate = LocalDate.of(2024, 10, 14);
        LocalDate endDate = LocalDate.of(2024, 10, 15);

        //To check whether data exist previously
        //We check if the first record exist
        if (em.find(Reservation.class, 1l) == null) {
            reservationSessionBeanLocal.createNewReservation(new Reservation(startDate, endDate));
        }
    }
    
}
