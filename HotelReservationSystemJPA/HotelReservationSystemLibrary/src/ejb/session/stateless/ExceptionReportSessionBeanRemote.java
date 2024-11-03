/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author JorJo
 */
@Remote
public interface ExceptionReportSessionBeanRemote {
    
    public ExceptionReport createNewExceptionReport(ExceptionReport exceptionReport);
    
    public List<ExceptionReport> retrieveAllExceptionReports();
    
}
