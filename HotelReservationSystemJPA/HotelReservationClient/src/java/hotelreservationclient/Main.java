/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotelreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author JorJo
 */
public class Main {

    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBeanRemote;

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;
    
    @EJB
    private static CustomerSessionBeanRemote customerSessionBeanRemote;
    
    @EJB
    private static RoomSessionBeanRemote roomSessionBeanRemote;
     
    @EJB
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;
    
    @EJB
    private static RoomRateReservationSessionBeanRemote roomRateReservationSessionBeanRemote;
    
    
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(guestSessionBeanRemote, customerSessionBeanRemote, roomSessionBeanRemote, roomRateSessionBeanRemote, roomTypeSessionBeanRemote, reservationSessionBeanRemote, roomRateReservationSessionBeanRemote);
        mainApp.runApp();
    }
    
}
