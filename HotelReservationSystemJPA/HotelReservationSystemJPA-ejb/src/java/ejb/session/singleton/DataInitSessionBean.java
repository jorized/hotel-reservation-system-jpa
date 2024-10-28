/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Customer;
import entity.Employee;
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
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.ReservationTypeEnum;

/**
 *
 * @author JorJo
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

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
        
        
        //Test to create an employee
        Employee testEmployee = new Employee("jordanemployee", "password", EmployeeAccessRightEnum.GUEST_RELATION_OFFICER);
        Employee testEmployee1 = new Employee("zhengjieemployee", "password", EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR);
        Employee testEmployee2 = new Employee("feltonemployee", "password", EmployeeAccessRightEnum.OPERATION_MANAGER);
        Employee testEmployee3 = new Employee("jarenemployee", "password", EmployeeAccessRightEnum.SALES_MANAGER);
        if (em.createQuery("SELECT COUNT(e) FROM Employee e", Long.class).getSingleResult() == 0) {
            try {
                 employeeSessionBeanLocal.createNewEmployee(testEmployee);
                 employeeSessionBeanLocal.createNewEmployee(testEmployee1);
                 employeeSessionBeanLocal.createNewEmployee(testEmployee2);
                 employeeSessionBeanLocal.createNewEmployee(testEmployee3);
            } catch (Exception ex) {
                System.out.println("Error creating employee: " + ex.getMessage()  + "\n");
            }

        }   
        
        //Test to create a reservation
        
        
        Customer testGuest = new Customer("Jordan", "Lim", "jorized@gmail.com", "98337602", "PASSPORTNO123", "jorized", "password123");
        if (em.createQuery("SELECT COUNT(c) FROM Customer c", Long.class).getSingleResult() == 0) {
            customerSessionBeanLocal.createNewCustomer(testGuest);
        }                
        
        RoomType testDeluxeRoomType = new RoomType(
            "Deluxe Room", 
            "Large", 
            "Super Single", 
            "Spacious room with city view", 
            500, 
            "Wi-Fi, TV, Mini-bar", 
            1
        );

        RoomType testPremierRoomType = new RoomType(
            "Premier Room", 
            "Large", 
            "Queen", 
            "Elegant room with luxurious amenities and city view", 
            500, 
            "Wi-Fi, TV, Mini-bar, Coffee Machine", 
            2
        );

        RoomType testFamilyRoomType = new RoomType(
            "Family Room", 
            "Extra Large", 
            "Double Queen", 
            "Spacious room suitable for families, with extra bedding options", 
            500, 
            "Wi-Fi, TV, Mini-bar, Kid-friendly amenities, Microwave", 
            3
        );

        RoomType testJuniorSuiteType = new RoomType(
            "Junior Suite", 
            "Suite", 
            "King", 
            "Luxurious suite with separate living area and stunning views", 
            500, 
            "Wi-Fi, TV, Mini-bar, Kitchenette, Living Room, Workspace", 
            4
        );

        RoomType testGrandSuiteType = new RoomType(
            "Grand Suite", 
            "Suite", 
            "King", 
            "Expansive suite with premium amenities and panoramic views", 
            500, 
            "Wi-Fi, TV, Mini-bar, Full Kitchen, Dining Area, Private Balcony, Jacuzzi", 
            5
        );

        //Inserting data for the 5 default room types
        if (em.createQuery("SELECT COUNT(rt) FROM RoomType rt", Long.class).getSingleResult() == 0) {
            try {
                roomTypeSessionBeanLocal.createNewRoomType(testDeluxeRoomType);
                roomTypeSessionBeanLocal.createNewRoomType(testPremierRoomType);
                roomTypeSessionBeanLocal.createNewRoomType(testFamilyRoomType);
                roomTypeSessionBeanLocal.createNewRoomType(testJuniorSuiteType);
                roomTypeSessionBeanLocal.createNewRoomType(testGrandSuiteType);
            } catch (Exception ex) {
                System.out.println("Error creating room type: " + ex.getMessage()  + "\n");
            }

        }         
        
        Room testRoom = new Room("20", "15", testDeluxeRoomType);
        if (em.createQuery("SELECT COUNT(r) FROM Room r", Long.class).getSingleResult() == 0) {
            try {
                roomSessionBeanLocal.createNewRoom(testRoom);
            } catch (Exception ex) {
                System.out.println("Error creating room: " + ex.getMessage()  + "\n");
            }
        }                                                       
        
        Reservation testReservation = new Reservation(startDate, endDate, ReservationTypeEnum.ONLINE, reservationAmount, testGuest, testDeluxeRoomType);
        if (em.createQuery("SELECT COUNT(r) FROM Reservation r", Long.class).getSingleResult() == 0) {
            reservationSessionBeanLocal.createNewReservation(testReservation);
        }
    }

}
