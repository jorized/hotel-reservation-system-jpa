/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.RoomRateReservationSessionBeanLocal;
import entity.RoomRateReservation;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.ejb.Stateless;

/**
 *
 * @author JorJo
 */
@WebService(serviceName = "RoomRateReservationWebService")
@Stateless()
public class RoomRateReservationWebService {

    @EJB
    private RoomRateReservationSessionBeanLocal roomRateReservationSessionBeanLocal;        

    @WebMethod(operationName = "createNewRoomRateReservation")
    public RoomRateReservation createNewRoomRateReservation(RoomRateReservation newRoomRateReservation) {
        return roomRateReservationSessionBeanLocal.createNewRoomRateReservation(newRoomRateReservation);
    }
}
