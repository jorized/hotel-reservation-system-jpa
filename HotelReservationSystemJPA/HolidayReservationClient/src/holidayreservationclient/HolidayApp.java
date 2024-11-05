/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package holidayreservationclient;

import java.util.List;
import java.util.Scanner;
import ws.partner.InvalidLoginCredentialsException;
import ws.partner.InvalidLoginCredentialsException_Exception;
import ws.partner.Partner;
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
    
    private Partner currentPartner;
    
    public HolidayApp() {
        this.currentPartner = null;
    }

    public HolidayApp(ReservationWebService_Service reservationService, RoomTypeWebService_Service roomTypeService, PartnerWebService_Service partnerService, RoomWebService_Service roomService, RoomRateWebService_Service roomRateService) {
        this();
        this.reservationService = reservationService;
        this.roomTypeService = roomTypeService;
        this.partnerService = partnerService;
        this.roomService = roomService;
        this.roomRateService = roomRateService;
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to the Holiday Reservation System ***\n");
            System.out.println("1: Login as partner");
            System.out.println("2: Search hotel room");
            System.out.println("3: Exit \n");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("Enter your choice > ");
                response = scanner.nextInt();

                if (response == 1) {
                    doPartnerLogin();
                } else if (response == 2) {
                    doSearchHotelRoom();
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option. Please try again. \n");
                }
            }

            if (response == 3) {
                break;
            }
        }
    }
    
    private void doPartnerLogin() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** Holiday Reservation System :: Login as partner ***\n");

            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            if (username.length() > 0 && password.length() > 0) {
                currentPartner = partnerService.getPartnerWebServicePort().partnerLogin(username, password);
                doAfterPartnerLogin();
            } else {                    
                InvalidLoginCredentialsException faultInfo = new InvalidLoginCredentialsException();
                throw new InvalidLoginCredentialsException_Exception("Invalid login credentials", faultInfo);
            }
        } catch (Exception ex) {
            System.out.println("Error logging in: " + ex.getMessage() + "\n");
        }
    }
    
    private void doAfterPartnerLogin() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                if (currentPartner == null) {
                    break;
                }

                System.out.println("*** Holiday Reservation System ::  Welcome, " + currentPartner.getUsername() + ". What can i do for you today?***\n");
                System.out.println("1: Search hotel room");
                System.out.println("2: Reserve hotel room");
                System.out.println("3: View my reservation details");
                System.out.println("4: View all my reservations");
                System.out.println("5: Logout\n");
                response = 0;

                while (response < 1 || response > 4) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doSearchHotelRoom();
                    } else if (response == 2) {
                        //doReserveHotelRoom();
                    } else if (response == 3) {
                        //doViewMyReservationDetails();
                    } else if (response == 4) {
                        //doViewAllMyReservations();
                    } else if (response == 5) {
                        System.out.println("Logging out...\n");
                        currentPartner = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }

            }
        } catch (Exception ex) {
            System.out.println("Error performing partner actions: " + ex.getMessage() + "\n");
        }
    }
    
    private void doSearchHotelRoom() {
        
    }
    
    
}
