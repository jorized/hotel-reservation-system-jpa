/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
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
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    @Override
    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialsException {
        try {
            return em.createQuery("SELECT e FROM Employee e WHERE e.username = :username AND e.password = :password", Employee.class)
                     .setParameter("username", username)
                     .setParameter("password", password)
                     .getSingleResult();
        } catch (NoResultException ex) {
            throw new InvalidLoginCredentialsException("Invalid login credentials.");
        }
    }
    
    @Override
    public Employee createNewEmployee(Employee newEmployee) throws AccountAlreadyExistException {        

        Employee existingEmployee = em.createQuery("SELECT e FROM Employee e WHERE e.username = :username", Employee.class)
                          .setParameter("username", newEmployee.getUsername())
                          .getResultStream()
                          .findFirst()
                          .orElse(null);                
        
        if (existingEmployee != null) {
            throw new AccountAlreadyExistException("Employee with username '" + newEmployee.getUsername() + "' already exists.");
        }
        
	em.persist(newEmployee);
	em.flush();
	
	return newEmployee;
    }
    
    @Override
    public List<Employee> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT e FROM Employee e");
	
	return query.getResultList();
    }
    
}
