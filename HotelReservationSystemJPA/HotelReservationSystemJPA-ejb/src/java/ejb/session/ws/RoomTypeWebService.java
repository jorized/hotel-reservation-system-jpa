/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Room;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.enumeration.ReservationTypeEnum;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomTypeException;

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
    
    @WebMethod(operationName = "getLowestTierDailyRate")
    public BigDecimal getLowestTierDailyRate(Date date, ReservationTypeEnum reservationType, List<Room> rooms) throws InvalidRoomTypeException, InvalidRoomException {
        return roomTypeSessionBeanLocal.getLowestTierDailyRate(date, reservationType, rooms);
    }
    
}
