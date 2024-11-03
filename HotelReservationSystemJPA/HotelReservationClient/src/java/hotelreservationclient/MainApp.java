/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Customer;
import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import util.enumeration.ReservationStatusEnum;
import util.enumeration.ReservationTypeEnum;
import util.enumeration.RoomStatusEnum;
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
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;

    private Customer currentCustomer;

    public MainApp() {
        this.currentCustomer = null;
    }

    public MainApp(
            GuestSessionBeanRemote guestSessionBeanRemote,
            CustomerSessionBeanRemote customerSessionBeanRemote,
            RoomSessionBeanRemote roomSessionBeanRemote,
            RoomRateSessionBeanRemote roomRateSessionBeanRemote,
            RoomTypeSessionBeanRemote roomTypeSessionBeanRemote,
            ReservationSessionBeanRemote reservationSessionBeanRemote
    ) {
        this();
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
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
                        doReserveHotelRoom();
                    } else if (response == 3) {
                        doViewMyReservationDetails();
                    } else if (response == 4) {
                        doViewAllMyReservations();
                    } else if (response == 5) {
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

            if (firstName.length() > 0 && lastName.length() > 0 && emailAddress.length() > 0
                    && phoneNumber.length() > 0 && passportNumber.length() > 0 && username.length() > 0 && password.length() > 0) {
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
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Reservation Client :: Search Hotel Room ***\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date checkInDate = promptForDate(scanner, dateFormat, "Enter check in date (yyyy-MM-dd): ");
            Date checkOutDate;

            // Ensure check-out date is not the same as check-in date
            while (true) {
                checkOutDate = promptForDate(scanner, dateFormat, "Enter check out date (yyyy-MM-dd): ");
                if (!checkOutDate.equals(checkInDate)) {
                    break;
                }
                System.out.println("Check-out date cannot be the same as the check-in date. Please enter a different check-out date.");
            }
            System.out.print("Enter number of rooms you would like to book: ");
            String noOfRoomsString = scanner.nextLine().trim();

            Integer noOfRooms = Integer.parseInt(noOfRoomsString);

            List<Room> availableRooms = roomSessionBeanRemote.retrieveAllAvailableRooms();
            if (availableRooms == null || availableRooms.isEmpty()) {
                System.out.println("\nNo available rooms found for the selected dates.");
                return;
            }

            int availableRoomSize = availableRooms.size();

            if (availableRoomSize >= noOfRooms) {
                // display all the differnt room types from the available rooms with Room ID
                System.out.println("\nAvailable rooms for the selected dates:");

                Set<String> displayedRoomTypes = new HashSet<>();

                for (Room room : availableRooms) {
                    String roomTypeName = room.getRoomType().getTypeName();
                    if (!displayedRoomTypes.contains(roomTypeName)) {
                        System.out.println("Room Type: " + roomTypeName);
                        displayedRoomTypes.add(roomTypeName); // Track this room type as displayed
                    }
                }

                while (true) {
                    System.out.println("\nWould you like to reserve?");
                    System.out.println("1: Reserve Room(s)");
                    System.out.println("2: Go back");

                    System.out.print("\nEnter your choice > ");
                    int response = scanner.nextInt();
                    scanner.nextLine();;

                    if (response == 1) {
                        System.out.print("\nAre you sure you want to reserve this room? (Y/N): ");
                        String responseString = scanner.nextLine().trim();
                        if (responseString.toLowerCase().equals("y")) {
                            reserveRooms(checkInDate, checkOutDate, noOfRooms, availableRooms);
                        } else if (responseString.toLowerCase().equals("n")) {
                            System.out.println("Reservation cancelled.\n");
                        } else {
                            System.out.println("Invalid option. Please try again. \n");
                        }
                    } else if (response == 2) {
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }

                }

            } else {
                System.out.println("Not enough rooms are available. Available rooms: " + availableRoomSize);
            }

        } catch (Exception ex) {
            System.out.println("Error searching for hotel room: " + ex.getMessage() + "\n");
        }
    }

    private void doReserveHotelRoom() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Reservation Client :: Reserve Hotel Room ***\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Prompt for check-in date
            Date checkInDate = promptForDate(scanner, dateFormat, "Enter check-in date (yyyy-MM-dd): ");

            // Prompt for check-out date and ensure it's not the same as check-in date
            Date checkOutDate;
            while (true) {
                checkOutDate = promptForDate(scanner, dateFormat, "Enter check-out date (yyyy-MM-dd): ");
                if (!checkOutDate.equals(checkInDate)) {
                    break;
                }
                System.out.println("Check-out date cannot be the same as the check-in date. Please enter a different check-out date.");
            }

            // Prompt for number of rooms
            System.out.print("Enter number of rooms you would like to book: ");
            int noOfRooms = scanner.nextInt();
            scanner.nextLine(); // Consume newline left-over

            // Retrieve available rooms
            List<Room> availableRooms = roomSessionBeanRemote.retrieveAllAvailableRooms();
            if (availableRooms == null || availableRooms.isEmpty()) {
                System.out.println("\nNo available rooms found for the selected dates.");
                return;
            }

            // Proceed to book rooms if available rooms are enough
            int availableRoomSize = availableRooms.size();
            if (availableRoomSize >= noOfRooms) {
                System.out.println("\nAvailable rooms found. Proceeding with reservation...");
                reserveRooms(checkInDate, checkOutDate, noOfRooms, availableRooms);
            } else {
                System.out.println("Not enough rooms are available. Available rooms: " + availableRoomSize);
            }

        } catch (Exception ex) {
            System.out.println("Error in reserving hotel room: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void reserveRooms(Date checkInDate, Date checkOutDate, int noOfRooms, List<Room> availableRooms) {
        try {
            if (reservationSessionBeanRemote == null) {
                System.out.println("Error: reservationSessionBeanRemote is not initialized.");
                return;
            }
            if (roomRateSessionBeanRemote == null) {
                System.out.println("Error: roomRateSessionBeanRemote is not initialized.");
                return;
            }
            if (currentCustomer == null) {
                System.out.println("Error: you must login to reserve.");
                return;
            }
            if (availableRooms == null || availableRooms.isEmpty()) {
                System.out.println("Error: No available rooms provided.");
                return;
            }

            // Group rooms by RoomType
            Map<RoomType, List<Room>> roomsByType = availableRooms.stream()
                    .collect(Collectors.groupingBy(Room::getRoomType));

            int roomsToBook = noOfRooms;

            for (Map.Entry<RoomType, List<Room>> entry : roomsByType.entrySet()) {
                RoomType roomType = entry.getKey();
                List<Room> roomsOfThisType = entry.getValue();

                //stop when all rooms booked
                if (roomsToBook <= 0) {
                    break;
                }

                int availableOfThisType = roomsOfThisType.size();
                int roomsToBookFromThisType = Math.min(roomsToBook, availableOfThisType);
                roomsToBook -= roomsToBookFromThisType;

                //calculate total amount for each roomType
                BigDecimal totalAmountForType = BigDecimal.ZERO;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(checkInDate);

                while (calendar.getTime().before(checkOutDate)) {
                    Date currentDate = calendar.getTime();

                    //get daily rate for date and roomType
                    BigDecimal ratePerNight = roomTypeSessionBeanRemote.getLowestTierDailyRate(currentDate, ReservationTypeEnum.ONLINE, availableRooms);

                    // Log the retrieved rate per night
                    // System.out.println("Rate per night for " + currentDate + " is $" + ratePerNight);
                    totalAmountForType = totalAmountForType.add(ratePerNight.multiply(BigDecimal.valueOf(roomsToBookFromThisType)));

                    // Log the running total amount after each day's rate is added
                    // System.out.println("Total amount so far: $" + totalAmountForType);
                    // Move to the next day
                    calendar.add(Calendar.DATE, 1);
                }
                
                System.out.println("Total reservation amount for Room Type: " + roomType.getTypeName()
                        + " with " + roomsToBookFromThisType + " room(s) and total amount: $" + totalAmountForType);

                // Create and persist the Reservation instance for this room type
                Reservation reservation = new Reservation(
                        checkInDate,
                        checkOutDate,
                        roomsToBookFromThisType,
                        ReservationTypeEnum.ONLINE,
                        totalAmountForType,
                        ReservationStatusEnum.CONFIRMED,
                        new Date(),
                        currentCustomer,
                        roomType,
                        null
                );

                Reservation newReservation = reservationSessionBeanRemote.createNewReservation(reservation);
                System.out.println("Reservation with Reservation ID : " +  newReservation.getReservationId() + " created successfully for Room Type '" + roomType.getTypeName() + "'");

                //mark the selected rooms as "reserved"
                for (int i = 0; i < roomsToBookFromThisType && i < roomsOfThisType.size(); i++) {
                    Room room = roomsOfThisType.get(i);
                    room.setRoomStatus(RoomStatusEnum.RESERVED);
                    roomSessionBeanRemote.updateRoom(room);

                    // System.out.println("Room ID: " + room.getRoomId() + " updated to status: " + room.getRoomStatus());
                }

                //check if allocation is same-day check-in after 2 am
                Calendar currentCal = Calendar.getInstance();
                if (reservationSessionBeanRemote.isSameDayCheckIn(checkInDate, currentCal.getTime()) && currentCal.get(Calendar.HOUR_OF_DAY) >= 2) {
                    roomSessionBeanRemote.allocateRooms();
                }
            }

            if (roomsToBook > 0) {
                System.out.println("Warning: Could not fulfill the complete room request. Remaining rooms needed: " + roomsToBook);
            }

        } catch (Exception ex) {
            System.out.println("Error reserving hotel room: " + ex.getMessage());
        }
    }

    private void doViewMyReservationDetails() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: View reservation details ***\n");

            displayReservationDetails();

            System.out.print("Enter reservation ID to view details: ");
            Long reservationId = scanner.nextLong();
            scanner.nextLine();

            Reservation reservation = reservationSessionBeanRemote.getReservationByReservationId(reservationId);

            if (reservation == null) {
                System.out.println("Reservation with ID " + reservationId + "not found.");
                return;
            }

            System.out.println("\nReservation Details:");
            System.out.println("Check-in Date: " + reservation.getCheckInDate());
            System.out.println("Check-out Date: " + reservation.getCheckOutDate());
            System.out.println("Room Type: " + reservation.getRoomType().getTypeName());
            System.out.println("Number of Rooms: " + reservation.getNumOfRooms());
            System.out.println("Total Amount: $" + reservation.getReservationAmount());

        } catch (Exception ex) {
            System.out.println("Error viewing room type: " + ex.getMessage() + "\n");
        }
    }

    private void doViewAllMyReservations() {
        try {
            System.out.println("*** HoRS Reservation Client :: View all reservations***\n");
            System.out.printf("%-5s %-40s %-40s %-20s %-20s %-20s\n",
                    "No.", "Check-in Date", "Check-out Date", "Room Type", "Number of Rooms", "Total Amount");
            int index = 1;

            List<Reservation> reservations = reservationSessionBeanRemote.retrieveAllReservationsByGuest(currentCustomer);

            if (reservations.isEmpty()) {
                System.out.println("No room rates found.");
            } else {
                for (Reservation reservation : reservations) {
                    System.out.printf("%-5s %-40s %-40s %-20s %-20s %-20s\n",
                            index++,
                            reservation.getCheckInDate(),
                            reservation.getCheckOutDate(),
                            reservation.getRoomType().getTypeName(),
                            reservation.getNumOfRooms(),
                            "$" + reservation.getReservationAmount());
                }
            }

            System.out.print("\n");

        } catch (Exception ex) {
            System.out.println("Error viewing all reservations: " + ex.getMessage() + "\n");
        }
    }

    /**
     * HELPER METHOD *
     */
    private Date promptForDate(Scanner scanner, SimpleDateFormat dateFormat, String prompt) {
        Date date = null;
        while (date == null) {
            System.out.print(prompt);
            String dateInput = scanner.nextLine().trim();
            try {
                date = dateFormat.parse(dateInput);

                // Check if the date is today or later
                Date today = new Date();
                if (date.before(dateFormat.parse(dateFormat.format(today)))) {
                    System.out.println("Date must be today or a future date. Please enter a valid date.");
                    date = null; // Reset date to null to repeat the loop
                }
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
            }
        }
        return date;
    }

    private void displayReservationDetails() {
        try {

            List<Reservation> reservations = reservationSessionBeanRemote.retrieveAllReservationsByGuest(currentCustomer);

            if (reservations.isEmpty()) {
                System.out.println("No reservations found.");
                return;
            }

            System.out.println("Reservation Summary:");
            for (int i = 0; i < reservations.size(); i++) {
                Reservation reservation = reservations.get(i);
                System.out.println("Reservation Id: "
                        + reservation.getReservationId() + " -- "
                        + reservation.getNumOfRooms() + " room(s), "
                        + reservation.getRoomType().getTypeName() + ", Total Amount: $"
                        + reservation.getReservationAmount());
            }
        } catch (Exception ex) {
            System.out.println("Error retrieving reservation details: " + ex.getMessage());
        }
    }

}
