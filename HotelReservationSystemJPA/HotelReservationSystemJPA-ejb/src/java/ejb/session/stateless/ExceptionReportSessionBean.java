/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.ExceptionReport;
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
public class ExceptionReportSessionBean implements ExceptionReportSessionBeanRemote, ExceptionReportSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystemJPA-ejbPU")
    private EntityManager em;

    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public ExceptionReport createNewExceptionReport(ExceptionReport exceptionReport) {

        em.persist(exceptionReport);
        em.flush();

        return exceptionReport;
    }
    
    @Override
    public List<ExceptionReport> retrieveAllExceptionReports() {
        Query query = em.createQuery("SELECT er FROM ExceptionReport er");
	
	return query.getResultList();
    }
    
}
