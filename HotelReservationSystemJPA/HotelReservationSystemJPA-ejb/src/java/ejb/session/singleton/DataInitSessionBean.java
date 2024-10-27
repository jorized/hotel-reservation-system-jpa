/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Reservation;
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

        //To check whether data exist previously
        //We check if the first record exist
        if (em.find(Reservation.class, 1l) == null) {
            reservationSessionBeanLocal.createNewReservation(new Reservation(startDate, endDate, ReservationTypeEnum.ONLINE, reservationAmount));
        }
    }

}
