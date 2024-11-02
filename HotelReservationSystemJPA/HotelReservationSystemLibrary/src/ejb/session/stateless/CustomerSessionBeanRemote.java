/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Remote;
import util.exception.AccountAlreadyExistException;
import util.exception.InvalidLoginCredentialsException;


/**
 *
 * @author JorJo
 */
@Remote
public interface CustomerSessionBeanRemote {

    public Customer createNewCustomer(Customer newCustomer) throws AccountAlreadyExistException;

    public Customer customerLogin(String username, String password) throws InvalidLoginCredentialsException;

}
