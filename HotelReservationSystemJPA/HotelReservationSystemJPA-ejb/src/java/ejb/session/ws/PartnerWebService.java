/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Partner;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.ejb.Stateless;
import util.exception.InvalidLoginCredentialsException;

/**
 *
 * @author JorJo
 */
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
        
    @WebMethod(operationName = "retrieveAllPartners")
    public List<Partner> retrieveAllPartners() {
        return partnerSessionBeanLocal.retrieveAllPartners();
    }
    
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(String username, String password) throws InvalidLoginCredentialsException {
        return partnerSessionBeanLocal.partnerLogin(username, password);
    }
}
