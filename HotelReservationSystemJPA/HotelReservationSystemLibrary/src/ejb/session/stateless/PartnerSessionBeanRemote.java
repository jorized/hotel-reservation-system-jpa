/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AccountAlreadyExistException;
import util.exception.InvalidLoginCredentialsException;

/**
 *
 * @author JorJo
 */
@Remote
public interface PartnerSessionBeanRemote {
    
    public Partner createNewPartner(Partner newPartner) throws AccountAlreadyExistException;
    
    public List<Partner> retrieveAllPartners();
    
    public Partner partnerLogin(String username, String password) throws InvalidLoginCredentialsException;
}
