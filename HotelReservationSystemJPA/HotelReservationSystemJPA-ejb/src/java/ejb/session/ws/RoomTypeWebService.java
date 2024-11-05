/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.RoomType;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author JorJo
 */
@WebService(serviceName = "RoomTypeWebService")
@Stateless()
public class RoomTypeWebService {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;    
    
    @WebMethod(operationName = "retrieveAllRoomTypes")
    public List<RoomType> retrieveAllRoomTypes() {
        return roomTypeSessionBeanLocal.retrieveAllRoomTypes();
    }
}
