/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package holidayreservationclient;

import java.util.List;
import ws.partner.PartnerWebService_Service;
import ws.reservation.ReservationWebService_Service;
import ws.room.RoomWebService_Service;
import ws.roomrate.RoomRateWebService_Service;
import ws.roomratereservation.RoomRateReservationWebService_Service;
import ws.roomtype.RoomType;
import ws.roomtype.RoomTypeWebService_Service;
/**
 *
 * @author JorJo
 */
public class HolidayReservationClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Initialising all the web services we need
        ReservationWebService_Service reservationService = new ReservationWebService_Service();
        RoomTypeWebService_Service roomTypeService = new RoomTypeWebService_Service();
        PartnerWebService_Service partnerService = new PartnerWebService_Service();
        RoomWebService_Service roomService = new RoomWebService_Service();
        RoomRateWebService_Service roomRateService = new RoomRateWebService_Service();
        RoomRateReservationWebService_Service roomRateReservationService = new RoomRateReservationWebService_Service();
        
        HolidayApp holidayApp = new HolidayApp(reservationService, roomTypeService, partnerService, roomService, roomRateService, roomRateReservationService);
        holidayApp.runApp();
        
    }
    
}
