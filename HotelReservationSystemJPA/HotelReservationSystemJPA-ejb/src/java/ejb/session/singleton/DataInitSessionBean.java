/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Customer;
import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.ReservationTypeEnum;
import util.enumeration.RoomTypeNameEnum;

/**
 *
 * @author JorJo
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
                

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PostConstruct
    public void postConstruct() {

        // Create new dates using java.util.Date
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.OCTOBER, 14);
        Date startDate = calendar.getTime();

        calendar.set(2024, Calendar.OCTOBER, 15);
        Date endDate = calendar.getTime();
        
        BigDecimal reservationAmount = new BigDecimal("1000");                
        
        BigDecimal testCapacity = new BigDecimal("500");
        
        //Test to create a reservation
        
        
        Customer testGuest = new Customer("Jordan", "Lim", "jorized@gmail.com", "98337602", "PASSPORTNO123", "jorized", "password123");
        if (em.createQuery("SELECT COUNT(c) FROM Customer c", Long.class).getSingleResult() == 0) {
            customerSessionBeanLocal.createNewCustomer(testGuest);
        }                
        
        RoomType testRoomType = new RoomType(RoomTypeNameEnum.DELUXE, "1000", "Super Single", "Very good", testCapacity, testCapacity, "amenities");
        if (em.createQuery("SELECT COUNT(rt) FROM RoomType rt", Long.class).getSingleResult() == 0) {
            roomTypeSessionBeanLocal.createNewRoomType(testRoomType);
        }         
        
        Room testRoom = new Room("20", "15", testRoomType);
        if (em.createQuery("SELECT COUNT(r) FROM Room r", Long.class).getSingleResult() == 0) {
            roomSessionBeanLocal.createNewRoom(testRoom);
        }                                                       
        
        Reservation testReservation = new Reservation(startDate, endDate, ReservationTypeEnum.ONLINE, reservationAmount, testGuest, testRoomType);
        if (em.createQuery("SELECT COUNT(r) FROM Reservation r", Long.class).getSingleResult() == 0) {
            reservationSessionBeanLocal.createNewReservation(testReservation);
        }
    }

}
