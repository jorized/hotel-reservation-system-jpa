/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package holidayreservationclient;

import java.util.List;
import ws.partner.PartnerWebService_Service;
import ws.reservation.ReservationWebService_Service;
import ws.room.RoomWebService_Service;
import ws.roomrate.RoomRateWebService_Service;
import ws.roomtype.RoomType;
import ws.roomtype.RoomTypeWebService_Service;

/**
 *
 * @author JorJo
 */
public class HolidayApp {
    
    private ReservationWebService_Service reservationService;
    private RoomTypeWebService_Service roomTypeService;
    private PartnerWebService_Service partnerService;
    private RoomWebService_Service roomService;
    private RoomRateWebService_Service roomRateService;

    public HolidayApp(ReservationWebService_Service reservationService, RoomTypeWebService_Service roomTypeService, PartnerWebService_Service partnerService, RoomWebService_Service roomService, RoomRateWebService_Service roomRateService) {
        this.reservationService = reservationService;
        this.roomTypeService = roomTypeService;
        this.partnerService = partnerService;
        this.roomService = roomService;
        this.roomRateService = roomRateService;
    }
    
    public void runApp() {
        List<RoomType> roomTypes = roomTypeService.getRoomTypeWebServicePort().retrieveAllRoomTypes();
        
        for (RoomType roomType : roomTypes) {
            System.out.println("Room type : " + roomType.getTypeName());
        }
    }
    
    
}
