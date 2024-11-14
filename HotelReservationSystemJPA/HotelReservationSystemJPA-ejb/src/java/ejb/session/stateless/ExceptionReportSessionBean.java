/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.RoomReservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidExceptionReportException;

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
    
    @Override
    public ExceptionReport retrieveExceptionReportByRoomReservation(RoomReservation roomReservation) throws InvalidExceptionReportException {
        // Assuming you’re using a query to retrieve the exception report
        try {
            return em.createQuery("SELECT e FROM ExceptionReport e WHERE e.roomReservation = :roomReservation", ExceptionReport.class)
                                .setParameter("roomReservation", roomReservation)
                                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    
}
