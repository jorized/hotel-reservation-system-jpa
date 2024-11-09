/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.ejb.Stateless;
import util.exception.InvalidReservationIdException;

/**
 *
 * @author JorJo
 */
@WebService(serviceName = "ReservationWebService")
@Stateless()
public class ReservationWebService {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    
    @WebMethod(operationName = "retrieveAllReservations")
    public List<Reservation> retrieveAllReservations() {
        return reservationSessionBeanLocal.retrieveAllReservations();
    }
    
    @WebMethod(operationName = "createNewReservation")
    public Reservation createNewReservation(Reservation newReservation) {
        return reservationSessionBeanLocal.createNewReservation(newReservation);
    }
    
    @WebMethod(operationName = "retrieveAllReservationsByPartner")
    public List<Reservation> retrieveAllReservationsByPartner(Partner partner) {
        return reservationSessionBeanLocal.retrieveAllReservationsByPartner(partner);
    }
    
    @WebMethod(operationName = "getReservationByReservationId")
    public Reservation getReservationByReservationId(Long reservationId) throws InvalidReservationIdException {
        return reservationSessionBeanLocal.getReservationByReservationId(reservationId);
    }
    
    

}
