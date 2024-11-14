/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.RoomRateSessionBeanLocal;
import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.enumeration.ReservationTypeEnum;

/**
 *
 * @author JorJo
 */
@WebService(serviceName = "RoomRateWebService")
@Stateless()
public class RoomRateWebService {
    
    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;   
    
    @WebMethod(operationName = "retrieveAllRoomRates")
    public List<RoomRate> retrieveAllRoomRates() {
        return roomRateSessionBeanLocal.retrieveAllRoomRates();
    }
    
    @WebMethod(operationName = "getDailyRateRoomRate")
    public RoomRate getDailyRateRoomRate(Date date, RoomType roomType, ReservationTypeEnum reservationTypeEnum) {
        return roomRateSessionBeanLocal.getDailyRateRoomRate(date, roomType, reservationTypeEnum);
    }
    
    
}
