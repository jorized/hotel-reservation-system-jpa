/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Customer;
import entity.Employee;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
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
import util.enumeration.PartnerAccessRightEnum;
import util.enumeration.ReservationStatusEnum;
import util.enumeration.ReservationTypeEnum;
import util.enumeration.RoomRateTypeEnum;
import util.exception.AccountAlreadyExistException;

/**
 *
 * @author JorJo
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

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
                System.out.println("Error creating employee: " + ex.getMessage() + "\n");
            }

        }

        //Test to create a reservation
        Customer testGuest = new Customer("John", "Doe", "john.doe@example.com", "1234567890", "A1234567", "johndoe", "pass123");
        if (em.createQuery("SELECT COUNT(c) FROM Customer c", Long.class).getSingleResult() == 0) {
            try {
                customerSessionBeanLocal.createNewCustomer(testGuest);
            } catch (Exception ex) {
                System.out.println("Error creating customer: " + ex.getMessage() + "\n");
            }

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
                System.out.println("Error creating room type: " + ex.getMessage() + "\n");
            }

        }

        Room testRoom = new Room("20", "15", testDeluxeRoomType);
        Room testRoom1 = new Room("20", "16", testDeluxeRoomType);
        Room testRoom2 = new Room("20", "17", testDeluxeRoomType);

        Room testRoom3 = new Room("30", "01", testPremierRoomType);
        Room testRoom4 = new Room("50", "01", testGrandSuiteType);
        if (em.createQuery("SELECT COUNT(r) FROM Room r", Long.class).getSingleResult() == 0) {
            try {
                roomSessionBeanLocal.createNewRoom(testRoom);
                roomSessionBeanLocal.createNewRoom(testRoom1);
                roomSessionBeanLocal.createNewRoom(testRoom2);
                roomSessionBeanLocal.createNewRoom(testRoom3);
                roomSessionBeanLocal.createNewRoom(testRoom4);
            } catch (Exception ex) {
                System.out.println("Error creating room: " + ex.getMessage() + "\n");
            }
        }

        //By right supposed to have logic to check for reservation type. If it's walk-in, no peak or promo (These are for online only)
        //If it's online, need to check it's check-in date and check-out date and if there's a peak/promo rate which lies in the period, assign it
        //There must be no overlapping between peak and peak dates, and promo and promo dates. 
        //If a peak rate and promo rate has dates which are same, promo will take precedence.
        //A room type can have only one walk-in and one normal rate (Since these are no dates by default).
        //But for demonstration purposes, we are just use Normal Rate (Online base rate)
        calendar.set(2024, Calendar.NOVEMBER, 2, 0, 0, 0);
        Date promotionStartDate = calendar.getTime();
        calendar.set(2024, Calendar.NOVEMBER, 11, 23, 59, 59);
        Date promotionEndDate = calendar.getTime();
        calendar.set(2024, Calendar.DECEMBER, 1, 0, 0, 0);
        Date peakStartDate = calendar.getTime();
        calendar.set(2024, Calendar.DECEMBER, 12, 23, 59, 59);
        Date peakEndDate = calendar.getTime();

        RoomRate testdeluxePublishedRate = new RoomRate("Published Rate for Deluxe Room", RoomRateTypeEnum.PUBLISHED, new BigDecimal("80"), null, null, null, null, testDeluxeRoomType);
        RoomRate testdeluxeNormalRate = new RoomRate("Normal Rate for Deluxe Room", RoomRateTypeEnum.NORMAL, new BigDecimal("60"), null, null, null, null, testDeluxeRoomType);
        RoomRate testdeluxePeakRate = new RoomRate("Peak Rate for Deluxe Room", RoomRateTypeEnum.PEAK, new BigDecimal("100"), null, null, peakStartDate, peakEndDate, testDeluxeRoomType);
        RoomRate testdeluxePromoRate = new RoomRate("Promotion Rate for Deluxe Room", RoomRateTypeEnum.PROMOTION, new BigDecimal("50"), promotionStartDate, promotionEndDate, null, null, testDeluxeRoomType);

        RoomRate testPremierPublishedRate = new RoomRate("Published Rate for Premier Room", RoomRateTypeEnum.PUBLISHED, new BigDecimal("75"), null, null, null, null, testPremierRoomType);
        RoomRate testGrandSuitePublishedRate = new RoomRate("Published Rate for Grand Suite Room", RoomRateTypeEnum.PUBLISHED, new BigDecimal("200"), null, null, null, null, testGrandSuiteType);
        if (em.createQuery("SELECT COUNT(rr) FROM RoomRate rr", Long.class).getSingleResult() == 0) {
            try {
                roomRateSessionBeanLocal.createNewRoomRate(testdeluxePublishedRate);
                roomRateSessionBeanLocal.createNewRoomRate(testdeluxeNormalRate);
                roomRateSessionBeanLocal.createNewRoomRate(testdeluxePeakRate);
                roomRateSessionBeanLocal.createNewRoomRate(testdeluxePromoRate);

                roomRateSessionBeanLocal.createNewRoomRate(testPremierPublishedRate);
                roomRateSessionBeanLocal.createNewRoomRate(testGrandSuitePublishedRate);
            } catch (Exception ex) {
                System.out.println("Error creating room rate: " + ex.getMessage() + "\n");
            }
        }

        Partner testPartner = new Partner("jojo", "password", PartnerAccessRightEnum.EMPLOYEE);
        if (em.createQuery("SELECT COUNT(p) FROM Partner p", Long.class).getSingleResult() == 0) {
            try {
                partnerSessionBeanLocal.createNewPartner(testPartner);
                System.out.println("Partner 'jojo' created successfully.");
            } catch (AccountAlreadyExistException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        //Reservation testReservation = new Reservation(startDate, endDate, 3, ReservationTypeEnum.ONLINE, testdeluxeNormalRate.getRatePerNight(), ReservationStatusEnum.CONFIRMED, new Date(), testGuest, testDeluxeRoomType, testPartner);
        //if (em.createQuery("SELECT COUNT(r) FROM Reservation r", Long.class).getSingleResult() == 0) {
        //    reservationSessionBeanLocal.createNewReservation(testReservation);
        //}

    }

}
