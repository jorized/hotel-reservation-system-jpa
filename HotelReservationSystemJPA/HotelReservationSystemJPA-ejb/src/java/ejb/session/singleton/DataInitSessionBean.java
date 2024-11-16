/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RoomRateTypeEnum;
import util.enumeration.RoomStatusEnum;

/**
 *
 * @author JorJo
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;
    
    private RoomType testDeluxeRoomType;
    private RoomType testPremierRoomType;
    private RoomType testFamilyRoomType;
    private RoomType testJuniorSuiteType;
    private RoomType testGrandSuiteType;
    

    @PostConstruct
    public void postConstruct() {                        
        initialiseEmployees();
        initialiseRoomTypes();
        initialiseRoomRates();
        initialiseRooms();
    }
    
    private void initialiseEmployees() {
        Employee testEmployee = new Employee("sysadmin", "password", EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR);
        Employee testEmployee1 = new Employee("opmanager", "password", EmployeeAccessRightEnum.OPERATION_MANAGER);
        Employee testEmployee2 = new Employee("salesmanager", "password", EmployeeAccessRightEnum.SALES_MANAGER);
        Employee testEmployee3 = new Employee("guestrelo", "password", EmployeeAccessRightEnum.GUEST_RELATION_OFFICER);

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
    }
    
    private void initialiseRoomTypes() {
        this.testDeluxeRoomType = new RoomType("Deluxe Room", "Large", "Super Single", "Spacious room with city view", 500, "Wi-Fi, TV, Mini-bar", 1);
        this.testPremierRoomType = new RoomType("Premier Room", "Large", "Queen", "Elegant room with luxurious amenities and city view", 500, "Wi-Fi, TV, Mini-bar, Coffee Machine", 2);
        this.testFamilyRoomType = new RoomType("Family Room", "Extra Large", "Double Queen", "Spacious room suitable for families, with extra bedding options", 500, "Wi-Fi, TV, Mini-bar, Kid-friendly amenities, Microwave", 3);
        this.testJuniorSuiteType = new RoomType("Junior Suite", "Suite", "King", "Luxurious suite with separate living area and stunning views", 500, "Wi-Fi, TV, Mini-bar, Kitchenette, Living Room, Workspace", 4);
        this.testGrandSuiteType = new RoomType("Grand Suite", "Suite", "King", "Expansive suite with premium amenities and panoramic views", 500, "Wi-Fi, TV, Mini-bar, Full Kitchen, Dining Area, Private Balcony, Jacuzzi", 5);

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
    }

    private void initialiseRoomRates() {
        RoomRate deluxeRoomPublished = new RoomRate("Deluxe Room Published", RoomRateTypeEnum.PUBLISHED, BigDecimal.valueOf(100), null, null, null, null, testDeluxeRoomType);
        RoomRate deluxeRoomNormal = new RoomRate("Deluxe Room Normal", RoomRateTypeEnum.NORMAL, BigDecimal.valueOf(50), null, null, null, null, testDeluxeRoomType);
        
        RoomRate premierRoomPublished = new RoomRate("Premier Room Published", RoomRateTypeEnum.PUBLISHED, BigDecimal.valueOf(200), null, null, null, null, testPremierRoomType);
        RoomRate premierRoomNormal = new RoomRate("Premier Room Normal", RoomRateTypeEnum.NORMAL, BigDecimal.valueOf(100), null, null, null, null, testPremierRoomType);
        
        RoomRate familyRoomPublished = new RoomRate("Family Room Published", RoomRateTypeEnum.PUBLISHED, BigDecimal.valueOf(300), null, null, null, null, testFamilyRoomType);
        RoomRate familyRoomNormal = new RoomRate("Family Room Normal", RoomRateTypeEnum.NORMAL, BigDecimal.valueOf(150), null, null, null, null, testFamilyRoomType);
        
        RoomRate juniorSuitePublished = new RoomRate("Junior Suite Published", RoomRateTypeEnum.PUBLISHED, BigDecimal.valueOf(400), null, null, null, null, testJuniorSuiteType);
        RoomRate juniorSuiteNormal = new RoomRate("Junior Suite Normal", RoomRateTypeEnum.NORMAL, BigDecimal.valueOf(200), null, null, null, null, testJuniorSuiteType);
        
        RoomRate grandSuitePublished = new RoomRate("Grand Suite Published", RoomRateTypeEnum.PUBLISHED, BigDecimal.valueOf(500), null, null, null, null, testGrandSuiteType);
        RoomRate grandSuiteNormal = new RoomRate("Grand Suite Normal", RoomRateTypeEnum.NORMAL, BigDecimal.valueOf(250), null, null, null, null, testGrandSuiteType);

        if (em.createQuery("SELECT COUNT(rr) FROM RoomRate rr", Long.class).getSingleResult() == 0) {
            try {
                roomRateSessionBeanLocal.createNewRoomRate(deluxeRoomPublished);
                roomRateSessionBeanLocal.createNewRoomRate(deluxeRoomNormal);
                roomRateSessionBeanLocal.createNewRoomRate(premierRoomPublished);
                roomRateSessionBeanLocal.createNewRoomRate(premierRoomNormal);
                roomRateSessionBeanLocal.createNewRoomRate(familyRoomPublished);
                roomRateSessionBeanLocal.createNewRoomRate(familyRoomNormal);
                roomRateSessionBeanLocal.createNewRoomRate(juniorSuitePublished);
                roomRateSessionBeanLocal.createNewRoomRate(juniorSuiteNormal);
                roomRateSessionBeanLocal.createNewRoomRate(grandSuitePublished);
                roomRateSessionBeanLocal.createNewRoomRate(grandSuiteNormal);
            } catch (Exception ex) {
                System.out.println("Error creating room rate: " + ex.getMessage() + "\n");
            }
        }
    }
    
    private void initialiseRooms() {
        // Deluxe Rooms
        Room deluxeRoom0101 = new Room("01", "01", testDeluxeRoomType, RoomStatusEnum.AVAILABLE);
        Room deluxeRoom0201 = new Room("02", "01", testDeluxeRoomType, RoomStatusEnum.AVAILABLE);
        Room deluxeRoom0301 = new Room("03", "01", testDeluxeRoomType, RoomStatusEnum.AVAILABLE);
        Room deluxeRoom0401 = new Room("04", "01", testDeluxeRoomType, RoomStatusEnum.AVAILABLE);
        Room deluxeRoom0501 = new Room("05", "01", testDeluxeRoomType, RoomStatusEnum.AVAILABLE);

        // Premier Rooms
        Room premierRoom0102 = new Room("01", "02", testPremierRoomType, RoomStatusEnum.AVAILABLE);
        Room premierRoom0202 = new Room("02", "02", testPremierRoomType, RoomStatusEnum.AVAILABLE);
        Room premierRoom0302 = new Room("03", "02", testPremierRoomType, RoomStatusEnum.AVAILABLE);
        Room premierRoom0402 = new Room("04", "02", testPremierRoomType, RoomStatusEnum.AVAILABLE);
        Room premierRoom0502 = new Room("05", "02", testPremierRoomType, RoomStatusEnum.AVAILABLE);

        // Family Rooms
        Room familyRoom0103 = new Room("01", "03", testFamilyRoomType, RoomStatusEnum.AVAILABLE);
        Room familyRoom0203 = new Room("02", "03", testFamilyRoomType, RoomStatusEnum.AVAILABLE);
        Room familyRoom0303 = new Room("03", "03", testFamilyRoomType, RoomStatusEnum.AVAILABLE);
        Room familyRoom0403 = new Room("04", "03", testFamilyRoomType, RoomStatusEnum.AVAILABLE);
        Room familyRoom0503 = new Room("05", "03", testFamilyRoomType, RoomStatusEnum.AVAILABLE);

        // Junior Suites
        Room juniorSuite0104 = new Room("01", "04", testJuniorSuiteType, RoomStatusEnum.AVAILABLE);
        Room juniorSuite0204 = new Room("02", "04", testJuniorSuiteType, RoomStatusEnum.AVAILABLE);
        Room juniorSuite0304 = new Room("03", "04", testJuniorSuiteType, RoomStatusEnum.AVAILABLE);
        Room juniorSuite0404 = new Room("04", "04", testJuniorSuiteType, RoomStatusEnum.AVAILABLE);
        Room juniorSuite0504 = new Room("05", "04", testJuniorSuiteType, RoomStatusEnum.AVAILABLE);

        // Grand Suites
        Room grandSuite0105 = new Room("01", "05", testGrandSuiteType, RoomStatusEnum.AVAILABLE);
        Room grandSuite0205 = new Room("02", "05", testGrandSuiteType, RoomStatusEnum.AVAILABLE);
        Room grandSuite0305 = new Room("03", "05", testGrandSuiteType, RoomStatusEnum.AVAILABLE);
        Room grandSuite0405 = new Room("04", "05", testGrandSuiteType, RoomStatusEnum.AVAILABLE);
        Room grandSuite0505 = new Room("05", "05", testGrandSuiteType, RoomStatusEnum.AVAILABLE);
        
        
        if (em.createQuery("SELECT COUNT(r) FROM Room r", Long.class).getSingleResult() == 0) {
            try {
                roomSessionBeanLocal.createNewRoom(deluxeRoom0101);
                roomSessionBeanLocal.createNewRoom(deluxeRoom0201);
                roomSessionBeanLocal.createNewRoom(deluxeRoom0301);
                roomSessionBeanLocal.createNewRoom(deluxeRoom0401);
                roomSessionBeanLocal.createNewRoom(deluxeRoom0501);

                roomSessionBeanLocal.createNewRoom(premierRoom0102);
                roomSessionBeanLocal.createNewRoom(premierRoom0202);
                roomSessionBeanLocal.createNewRoom(premierRoom0302);
                roomSessionBeanLocal.createNewRoom(premierRoom0402);
                roomSessionBeanLocal.createNewRoom(premierRoom0502);

                roomSessionBeanLocal.createNewRoom(familyRoom0103);
                roomSessionBeanLocal.createNewRoom(familyRoom0203);
                roomSessionBeanLocal.createNewRoom(familyRoom0303);
                roomSessionBeanLocal.createNewRoom(familyRoom0403);
                roomSessionBeanLocal.createNewRoom(familyRoom0503);

                roomSessionBeanLocal.createNewRoom(juniorSuite0104);
                roomSessionBeanLocal.createNewRoom(juniorSuite0204);
                roomSessionBeanLocal.createNewRoom(juniorSuite0304);
                roomSessionBeanLocal.createNewRoom(juniorSuite0404);
                roomSessionBeanLocal.createNewRoom(juniorSuite0504);

                roomSessionBeanLocal.createNewRoom(grandSuite0105);
                roomSessionBeanLocal.createNewRoom(grandSuite0205);
                roomSessionBeanLocal.createNewRoom(grandSuite0305);
                roomSessionBeanLocal.createNewRoom(grandSuite0405);
                roomSessionBeanLocal.createNewRoom(grandSuite0505);
            } catch (Exception ex) {
                System.out.println("Error creating room: " + ex.getMessage() + "\n");
            }
        }
    }           

}
