/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotelmanagementclient;

import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Reservation;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author JorJo
 */
public class Main {

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;

    
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        List<Reservation> reservations = reservationSessionBeanRemote.retrieveAllReservations();
        
        for (Reservation reservation: reservations) {
            System.out.println("reservation= " + reservation.getReservationId()+ "; checkindate= " + reservation.getCheckInDate() + "; checkoutdate= " + reservation.getCheckOutDate());
        }
    }
    
}
