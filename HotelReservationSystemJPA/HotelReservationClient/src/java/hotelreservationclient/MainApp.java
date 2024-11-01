/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import entity.Customer;
import java.util.Scanner;
import util.exception.InvalidAccountCredentialsException;

/**
 *
 * @author JorJo
 */
public class MainApp {
    
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;       
    
    private Customer currentCustomer;
    
    public MainApp() {
        this.currentCustomer = null;
    }
    

    public MainApp(
            GuestSessionBeanRemote guestSessionBeanRemote, 
            CustomerSessionBeanRemote customerSessionBeanRemote, 
            RoomSessionBeanRemote roomSessionBeanRemote, 
            ReservationSessionBeanRemote reservationSessionBeanRemote
    ) {
        this();
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("*** Welcome to the HoRS Reservation Client ***\n");
            System.out.println("1: Login as guest");
            System.out.println("2: Register as guest");
            System.out.println("3: Search hotel room");
            System.out.println("4: Exit \n");
            response = 0;
            
            while (response < 1 || response > 4) {
                System.out.print("Enter your choice > ");
                response = scanner.nextInt();
                
                if (response == 1) {
                    doGuestLogin();
                } else if (response == 2) {
                    doGuestRegistration();
                } else if (response == 3) {
                    doSearchHotelRoom();
                } else if (response == 4) {
                    break;                    
                } else {
                    System.out.println("Invalid option. Please try again. \n");
                }                                
            }
            
            if (response == 4) {
                break;
            }
        }
    }
    
    private void doGuestLogin() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Reservation Client :: Login as guest ***\n");
            
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            
            if (username.length() > 0 && password.length() > 0) {
                currentCustomer = customerSessionBeanRemote.customerLogin(username, password);
                doAfterGuestLogin();
            } else {
                throw new InvalidAccountCredentialsException("Invalid account credentials");
            }
        } catch (Exception ex) {
            System.out.println("Error logging in: " + ex.getMessage() + "\n");
        }
    }
    
    private void doAfterGuestLogin() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            
            while (true) {
                
                if (currentCustomer == null) {
                    break;
                }
                
                System.out.println("*** HoRS Reservation Client ::  Welcome, " + currentCustomer.getUsername() + ". What can i do for you today?***\n");
                System.out.println("1: Search hotel room");
                System.out.println("2: View my reservation details");
                System.out.println("3: View all my reservations");
                System.out.println("4: Logout\n");
                response = 0;
                
                while (response < 1 || response > 4) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();
                    
                    if (response == 1) {
                        doSearchHotelRoom();
                    } else if (response == 2) {
                        doViewMyReservationDetails();
                    } else if (response == 3) {
                        doViewAllMyReservations();
                    } else if (response == 4) {
                        System.out.println("Logging out...\n");
                        currentCustomer = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }
                

            }
        } catch (Exception ex) {
            System.out.println("Error performing customer actions: " + ex.getMessage() + "\n");
        }
    }
    
    private void doViewMyReservationDetails() {
        
    }
    
    private void doViewAllMyReservations() {
        
    }
    
    private void doGuestRegistration() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Reservation Client :: Register as guest ***\n");
            
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine().trim();
            
            System.out.print("Enter email address: ");
            String emailAddress = scanner.nextLine().trim();
            
            System.out.print("Enter phone number: ");
            String phoneNumber = scanner.nextLine().trim();
            
            System.out.print("Enter passport number: ");
            String passportNumber = scanner.nextLine().trim();
            
            System.out.print("Enter passport number: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            
            if (firstName.length() > 0 && lastName.length() > 0 && emailAddress.length() > 0 && 
                    phoneNumber.length() > 0 && passportNumber.length() > 0 && username.length() > 0 && password.length() > 0) {
                Customer newCustomer = new Customer(firstName, lastName, emailAddress, phoneNumber, passportNumber, username, password);
                customerSessionBeanRemote.createNewCustomer(newCustomer);
                System.out.println("Your account has successfully been created!\n");
            } else {
                throw new InvalidAccountCredentialsException("Invalid account credentials");
            }
            
        } catch (Exception ex) {
            System.out.println("Error creating account: " + ex.getMessage() + "\n");
        }
    }
    
    private void doSearchHotelRoom() {
        try {
            
        } catch (Exception ex) {
            System.out.println("Error searching for hotel room: " + ex.getMessage() + "\n");
        }
    }
     
    
    
}
