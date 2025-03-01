/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package holidayreservationclient;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.partner.InvalidLoginCredentialsException;
import ws.partner.InvalidLoginCredentialsException_Exception;
import ws.partner.Partner;
import ws.partner.PartnerWebService_Service;
import ws.reservation.Reservation;
import ws.reservation.ReservationStatusEnum;
import ws.reservation.ReservationWebService_Service;
import ws.room.Room;
import ws.room.RoomStatusEnum;
import ws.room.RoomWebService_Service;
import ws.roomrate.RoomRate;
import ws.roomrate.RoomRateWebService_Service;
import ws.roomratereservation.RoomRateReservation;
import ws.roomratereservation.RoomRateReservationWebService_Service;
import ws.roomtype.ReservationTypeEnum;
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
    private RoomRateReservationWebService_Service roomRateReservationService;
    
    private Partner currentPartner;
    
    public HolidayApp() {
        this.currentPartner = null;
    }

    public HolidayApp(
            ReservationWebService_Service reservationService, 
            RoomTypeWebService_Service roomTypeService, 
            PartnerWebService_Service partnerService, 
            RoomWebService_Service roomService, 
            RoomRateWebService_Service roomRateService, 
            RoomRateReservationWebService_Service roomRateReservationService
    ) {
        this();
        this.reservationService = reservationService;
        this.roomTypeService = roomTypeService;
        this.partnerService = partnerService;
        this.roomService = roomService;
        this.roomRateService = roomRateService;
        this.roomRateReservationService = roomRateReservationService;
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
                        doReserveHotelRoom();
                    } else if (response == 3) {
                        doViewMyReservationDetails();
                    } else if (response == 4) {
                        doViewAllMyReservations();
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
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** Holiday Reservation System :: Search Hotel Room ***\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date checkInDate = promptForDate(scanner, dateFormat, "Enter check-in date (yyyy-MM-dd): ");
            Date checkOutDate;

            // Ensure check-out date is not the same as check-in date
            while (true) {
                checkOutDate = promptForDate(scanner, dateFormat, "Enter check-out date (yyyy-MM-dd): ");
                if (checkOutDate.after(checkInDate)) {
                    break;
                }
                System.out.println("Check-out date must be after the check-in date. Please enter a valid check-out date.");
            }
            System.out.print("Enter number of rooms you would like to book: ");
            int noOfRooms = Integer.parseInt(scanner.nextLine().trim());

            List<Room> availableRooms = roomService.getRoomWebServicePort().retrieveAllAvailableAndPredictedRooms(toXMLGregorianCalendar(checkInDate), toXMLGregorianCalendar(checkOutDate));
            if (availableRooms == null || availableRooms.isEmpty()) {
                System.out.println("\nNo available rooms found for the selected dates.");
                return;
            }

            // Call reserveRooms to display rooms and handle reservation if applicable
            reserveRooms(checkInDate, checkOutDate, noOfRooms, availableRooms);

        } catch (Exception ex) {
            System.out.println("Error in searching for hotel room: " + ex.getMessage());
        }
    }
    
    private void doReserveHotelRoom() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** Holiday Reservation System :: Reserve Hotel Room ***\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Prompt for check-in date
            Date checkInDate = promptForDate(scanner, dateFormat, "Enter check-in date (yyyy-MM-dd): ");

            // Prompt for check-out date and ensure it's not the same as check-in date
            Date checkOutDate;
            while (true) {
                checkOutDate = promptForDate(scanner, dateFormat, "Enter check-out date (yyyy-MM-dd): ");
                if (checkOutDate.after(checkInDate)) {
                    break;
                }
                System.out.println("Check-out date must be after the check-in date. Please enter a valid check-out date.");
            }

            System.out.print("Enter number of rooms you would like to book: ");
            int noOfRooms = Integer.parseInt(scanner.nextLine().trim());

            List<Room> availableRooms = roomService.getRoomWebServicePort().retrieveAllAvailableAndPredictedRooms(toXMLGregorianCalendar(checkInDate), toXMLGregorianCalendar(checkOutDate));
            if (availableRooms == null || availableRooms.isEmpty()) {
                System.out.println("\nNo available rooms found for the selected dates.");
                return;
            }

            int availableRoomSize = availableRooms.size();

            if (availableRoomSize >= noOfRooms) {
                System.out.println("\nAvailable rooms found. Proceeding with reservation...");
                reserveRooms(checkInDate, checkOutDate, noOfRooms, availableRooms);
            } else {
                System.out.println("Not enough rooms are available. Available rooms: " + availableRoomSize);
            }

        } catch (Exception ex) {
            System.out.println("Error in reserving hotel room: " + ex.getMessage());
        }
    }
    
    
    
    private void reserveRooms(Date checkInDate, Date checkOutDate, int noOfRooms, List<Room> availableRooms) {
    try {
        List<RoomType> roomTypes = new ArrayList<>();
        List<BigDecimal> totalPerRoomAmounts = new ArrayList<>();
        List<List<ws.roomtype.Room>> roomsByType = new ArrayList<>();
        // Map to store unique RoomRates per RoomType
        Map<RoomType, List<RoomRate>> uniqueRoomRatesMap = new HashMap<>();

        // Group rooms by RoomTypeId using maps to avoid duplicates
        Map<Long, List<ws.roomtype.Room>> roomsByTypeIdMap = new HashMap<>();
        Map<Long, RoomType> roomTypeMap = new HashMap<>();

        for (Room room : availableRooms) {
            RoomType roomType = convertToRoomtypeRoomType(room.getRoomType());
            Long roomTypeId = roomType.getRoomTypeId();

            roomTypeMap.putIfAbsent(roomTypeId, roomType);
            roomsByTypeIdMap.computeIfAbsent(roomTypeId, k -> new ArrayList<>()).add(convertToRoomtypeRoom(room));
        }

        // Populate roomTypes and roomsByType lists
        for (Long roomTypeId : roomsByTypeIdMap.keySet()) {
            RoomType roomType = roomTypeMap.get(roomTypeId);
            roomTypes.add(roomType);
            roomsByType.add(roomsByTypeIdMap.get(roomTypeId));
            // Initialize the uniqueRoomRatesMap for each RoomType
            uniqueRoomRatesMap.put(roomType, new ArrayList<>());
        }

        // Display available room types and calculate total prices
        long numberOfNights = (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24);
        System.out.println("\nAvailable rooms for the selected dates (" + numberOfNights + " nights):\n");
        List<Integer> selectableIndices = new ArrayList<>();

        for (int i = 0; i < roomTypes.size(); i++) {
            RoomType roomType = roomTypes.get(i);
            List<ws.roomtype.Room> roomsOfThisType = roomsByType.get(i);

            if (roomsOfThisType.size() >= noOfRooms) {
                selectableIndices.add(i);

                // Calculate the per-room price for this room type
                BigDecimal totalPerRoomAmount = BigDecimal.ZERO;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(checkInDate);

                while (calendar.getTime().before(checkOutDate)) {
                    Date currentDate = calendar.getTime();
                    
                    ws.roomrate.RoomType roomRateRoomType = convertToRoomRateRoomType(roomType);
                    
                    ws.roomrate.ReservationTypeEnum roomRateReservationType = convertToRoomRateReservationTypeEnum(ReservationTypeEnum.ONLINE);

                    // Retrieve the lowest tier daily rate
                    BigDecimal ratePerNight = roomTypeService.getRoomTypeWebServicePort()
                            .getLowestTierDailyRate(toXMLGregorianCalendar(currentDate), ReservationTypeEnum.ONLINE, roomsOfThisType);

                    //Retrieve the daily RoomRate
                    RoomRate dailyRoomRate = roomRateService.getRoomRateWebServicePort()
                            .getDailyRateRoomRate(toXMLGregorianCalendar(currentDate), roomRateRoomType, roomRateReservationType);

                    // Store unique RoomRates in the map
                    if (dailyRoomRate != null) {
                        List<RoomRate> roomRatesForType = uniqueRoomRatesMap.get(roomType);
                        if (!roomRatesForType.contains(dailyRoomRate)) {
                            roomRatesForType.add(dailyRoomRate);
                        }
                    }

                    if (ratePerNight != null) {
                        totalPerRoomAmount = totalPerRoomAmount.add(ratePerNight);
                    }
                    calendar.add(Calendar.DATE, 1);
                }

                totalPerRoomAmounts.add(totalPerRoomAmount);

                // Display each room type with a selection number
                System.out.println((selectableIndices.size()) + ": " + roomType.getTypeName() +
                        " - $" + totalPerRoomAmount +
                        " per room, Total Reservation Amount: $" +
                        totalPerRoomAmount.multiply(BigDecimal.valueOf(noOfRooms)) +
                        " for " + noOfRooms + ((noOfRooms > 1) ? " rooms" : " room"));
            }
        }

        if (selectableIndices.isEmpty()) {
            System.out.println("No available room types have enough rooms for the requested number: " + noOfRooms);
            return;
        }

        // Ask the user to select a room type from the displayed options
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nPlease select a room type by entering the number: ");
        int roomTypeSelection = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (roomTypeSelection < 1 || roomTypeSelection > selectableIndices.size()) {
            System.out.println("Invalid selection. Returning to the main menu.");
            return;
        }

        // Get the selected room type index and corresponding data
        int selectedIndex = selectableIndices.get(roomTypeSelection - 1);
        RoomType selectedRoomType = roomTypes.get(selectedIndex);
        BigDecimal selectedTotalPerRoomAmount = totalPerRoomAmounts.get(selectedIndex);

        System.out.println("\nYou selected: " + selectedRoomType.getTypeName() +
                " - $" + selectedTotalPerRoomAmount +
                " per room, Total Reservation Amount: $" +
                selectedTotalPerRoomAmount.multiply(BigDecimal.valueOf(noOfRooms)) +
                " for " + noOfRooms + " rooms");

        // Proceed with reservation confirmation
        System.out.println("\nWould you like to proceed with reservation?");
        System.out.println("1: Reserve Room(s)");
        System.out.println("2: Go back");

        System.out.print("\nEnter your choice > ");
        int response = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (response == 1) {
            if (currentPartner == null) {
                System.out.println("Error: you must login to reserve.");
                return;
            }

            // Re-fetch the latest available rooms after confirmation
            List<Room> latestAvailableRooms = roomService.getRoomWebServicePort().retrieveAllAvailableRooms();

            // Group the latest available rooms by RoomTypeId
            Map<Long, List<Room>> latestRoomsByTypeIdMap = new HashMap<>();
            for (Room room : latestAvailableRooms) {
                ws.roomtype.RoomType convertedRoomType = convertToRoomtypeRoomType(room.getRoomType());
                Long roomTypeId = convertedRoomType.getRoomTypeId();
                latestRoomsByTypeIdMap.computeIfAbsent(roomTypeId, k -> new ArrayList<>()).add(room);
            }

            // Calculate total reservation amount based on the selected room type and total number of rooms
            BigDecimal totalReservationAmount = selectedTotalPerRoomAmount.multiply(BigDecimal.valueOf(noOfRooms));

            int roomsToBook = noOfRooms;
            List<Room> roomsToReserve = new ArrayList<>();

            // Attempt to reserve rooms in the selected room type only
            List<Room> latestRoomsOfSelectedType = latestRoomsByTypeIdMap.getOrDefault(selectedRoomType.getRoomTypeId(), new ArrayList<>());
            int availableRoomsOfSelectedType = latestRoomsOfSelectedType.size();

            int roomsToBookFromSelectedType = Math.min(roomsToBook, availableRoomsOfSelectedType);
            roomsToBook -= roomsToBookFromSelectedType;

            // Mark rooms as RESERVED
            for (int j = 0; j < roomsToBookFromSelectedType; j++) {
                Room room = latestRoomsOfSelectedType.get(j);
                room.setRoomStatus(RoomStatusEnum.RESERVED);
                roomService.getRoomWebServicePort().updateRoom(room);
                roomsToReserve.add(room);
            }

            // Create the Reservation and get back the created Reservation
            Reservation reservation = new Reservation();
            reservation.setCheckInDate(toXMLGregorianCalendar(checkInDate));
            reservation.setCheckOutDate(toXMLGregorianCalendar(checkOutDate));
            reservation.setNumOfRooms(noOfRooms);
            reservation.setReservationType(convertToReservationTypeEnum(ReservationTypeEnum.ONLINE));
            reservation.setReservationAmount(totalReservationAmount);
            reservation.setReservationStatusEnum(ReservationStatusEnum.CONFIRMED);
            reservation.setRoomType(convertToReservationRoomType(selectedRoomType));
            reservation.setPartner(convertToReservationPartner(currentPartner));

            // Create the Reservation and get the newly created Reservation with ID
            Reservation newlyCreatedReservation = reservationService.getReservationWebServicePort().createNewReservation(reservation);

            // Create and persist RoomRateReservation records for each unique RoomRate
            List<RoomRate> roomRatesForType = uniqueRoomRatesMap.get(selectedRoomType);

            if (roomRatesForType != null) {
                for (RoomRate roomRate : roomRatesForType) {
                    // Create RoomRateReservation
                    RoomRateReservation roomRateReservation = new RoomRateReservation();
                    roomRateReservation.setRoomRate(convertToRoomRateReservationRoomRate(roomRate));
                    roomRateReservation.setReservation(convertToRoomRateReservation(newlyCreatedReservation));

                    // Persist the RoomRateReservation
                    roomRateReservationService.getRoomRateReservationWebServicePort().createNewRoomRateReservation(roomRateReservation);
                }
            }

            // Immediate allocation if applicable
            if (stripTime(convertToDate(reservation.getCheckInDate())).equals(stripTime(new Date()))) {
                Calendar currentTime = Calendar.getInstance();
                int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                if (currentHour >= 2) {
                    roomService.getRoomWebServicePort().allocateRooms();
                }
            }

            System.out.println("Your reservation has successfully been made!\n");
        } else if (response == 2) {
            System.out.println("Returning to the main menu...");
        } else {
            System.out.println("Invalid option. Please try again.");
        }
    } catch (Exception ex) {
        System.out.println("Error reserving hotel room: " + ex.getMessage());
        ex.printStackTrace();
    }
}



    
    private void doViewMyReservationDetails() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** Holiday Reservation System :: View reservation details ***\n");

            // Retrieve reservations for the current partner
            List<Reservation> reservations = reservationService.getReservationWebServicePort().retrieveAllReservationsByPartner(convertToReservationPartner(currentPartner));

            // Check if there are any reservations
            if (reservations == null || reservations.isEmpty()) {
                System.out.println("No reservations found.");
                return;
            }

            // Display a summary of reservations in a table format
            System.out.println("*** Reservation Summary ***\n");
            System.out.printf("%-5s %-20s %-20s\n", "No.", "Room Type", "Total Amount");

            int index = 1;
            for (Reservation reservation : reservations) {
                System.out.printf("%-5d %-20s %-20s\n",
                        index++,
                        reservation.getRoomType().getTypeName(),
                        "$" + reservation.getReservationAmount());
            }

            System.out.print("\nEnter the reservation index number to view details: ");
            int selectedIndex = scanner.nextInt();
            scanner.nextLine();

            // Validate index input
            if (selectedIndex < 1 || selectedIndex > reservations.size()) {
                System.out.println("Invalid index. Please try again.");
                return;
            }

            // Retrieve the selected reservation
            Reservation selectedReservation = reservations.get(selectedIndex - 1);

            // Display detailed reservation information
            System.out.println("\nReservation Details:");
            System.out.println("Check-in Date: " + formatDate(convertToDate(selectedReservation.getCheckInDate())));
            System.out.println("Check-out Date: " + formatDate(convertToDate(selectedReservation.getCheckOutDate())));
            System.out.println("Room Type: " + selectedReservation.getRoomType().getTypeName());
            System.out.println("Number of Rooms: " + selectedReservation.getNumOfRooms());
            System.out.println("Total Amount: $" + selectedReservation.getReservationAmount() + "\n");

        } catch (Exception ex) {
            System.out.println("Error viewing reservation details: " + ex.getMessage() + "\n");
        }
    }

    
    private void doViewAllMyReservations() {
        try {
            System.out.println("*** Holiday Reservation System :: View all reservations***\n");
            System.out.printf("%-5s %-20s %-20s %-20s %-20s %-20s\n",
                    "No.", "Check-in Date", "Check-out Date", "Room Type", "Number of Rooms", "Total Amount");
            System.out.println("---------------------------------------------------------------------------------------------------------");
            int index = 1;

            List<Reservation> reservations = reservationService.getReservationWebServicePort().retrieveAllReservationsByPartner(convertToReservationPartner(currentPartner));

            if (reservations.isEmpty()) {
                System.out.println("No reservations found.");
            } else {
                for (Reservation reservation : reservations) {
                    System.out.printf("%-5s %-20s %-20s %-20s %-20s %-20s\n",
                            index++,
                            formatDate(convertToDate(reservation.getCheckInDate())),
                            formatDate(convertToDate(reservation.getCheckOutDate())),
                            reservation.getRoomType().getTypeName(),
                            reservation.getNumOfRooms(),
                            "$" + reservation.getReservationAmount());
                }
                System.out.println("---------------------------------------------------------------------------------------------------------");
            }

            System.out.print("\n");

        } catch (Exception ex) {
            System.out.println("Error viewing all reservations: " + ex.getMessage() + "\n");
        }
    }
            
    /** HELPER METHODS **/
    private Date promptForDate(Scanner scanner, SimpleDateFormat dateFormat, String prompt) {
        dateFormat.setLenient(false);
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
    
    private Date stripTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    //In jakarta EE, container manages conversion between Date and XMLGregorianCalendar, but for Non jakarta EE client,
    //This conversion does not happen automatically because standard SOAP-based web services expect XMLGregorianCalendar
    public XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }        
    }
    
    private Date convertToDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return xmlGregorianCalendar.toGregorianCalendar().getTime();
    }


   private ws.roomtype.RoomType convertToRoomtypeRoomType(ws.room.RoomType roomType) {
        ws.roomtype.RoomType newRoomType = new ws.roomtype.RoomType();
        
        newRoomType.setRoomTypeId(roomType.getRoomTypeId());
        newRoomType.setTypeName(roomType.getTypeName());
        newRoomType.setTierNumber(roomType.getTierNumber());
        newRoomType.setDescription(roomType.getDescription());
        newRoomType.setBed(roomType.getBed());
        newRoomType.setAmenities(roomType.getAmenities());
        newRoomType.setCapacity(roomType.getCapacity());
        newRoomType.setSize(roomType.getSize());
        newRoomType.setRoomTypeStatus(convertToRoomtypeRoomTypeStatusEnum(roomType.getRoomTypeStatus()));

        
        return newRoomType;
    }
      
   
   private ws.roomtype.Room convertToRoomtypeRoom(ws.room.Room room) {
        ws.roomtype.Room convertedRoom = new ws.roomtype.Room();
        convertedRoom.setRoomType(convertToRoomtypeRoomType(room.getRoomType()));
        convertedRoom.setRoomNum(room.getRoomNum());
        convertedRoom.setRoomStatus(convertToRoomtypeRoomStatusEnum(room.getRoomStatus()));
        return convertedRoom;
    }
   
   private ws.reservation.RoomType convertToReservationRoomType(ws.roomtype.RoomType roomType) {
        ws.reservation.RoomType convertedRoomType = new ws.reservation.RoomType();
        convertedRoomType.setRoomTypeId(roomType.getRoomTypeId());
        convertedRoomType.setTypeName(roomType.getTypeName());
        convertedRoomType.setTierNumber(roomType.getTierNumber());
        convertedRoomType.setDescription(roomType.getDescription());
        convertedRoomType.setBed(roomType.getBed());
        convertedRoomType.setAmenities(roomType.getAmenities());
        convertedRoomType.setCapacity(roomType.getCapacity());
        convertedRoomType.setSize(roomType.getSize());
        convertedRoomType.setRoomTypeStatus(convertToReservationRoomTypeStatusEnum(roomType.getRoomTypeStatus()));
        return convertedRoomType;
    }

    private ws.roomtype.RoomTypeStatusEnum convertToRoomtypeRoomTypeStatusEnum(ws.room.RoomTypeStatusEnum status) {
        return ws.roomtype.RoomTypeStatusEnum.valueOf(status.name());
    }

   
    private ws.roomtype.RoomStatusEnum convertToRoomtypeRoomStatusEnum(ws.room.RoomStatusEnum roomStatus) {
        return ws.roomtype.RoomStatusEnum.valueOf(roomStatus.name());
    }
    
    private ws.reservation.ReservationTypeEnum convertToReservationTypeEnum(ws.roomtype.ReservationTypeEnum type) {
        return ws.reservation.ReservationTypeEnum.valueOf(type.name());
    }
    
    private ws.reservation.RoomTypeStatusEnum convertToReservationRoomTypeStatusEnum(ws.roomtype.RoomTypeStatusEnum status) {
        return ws.reservation.RoomTypeStatusEnum.valueOf(status.name());
    }    

    
    private ws.reservation.Partner convertToReservationPartner(ws.partner.Partner partner) {
        ws.reservation.Partner convertedPartner = new ws.reservation.Partner();
        convertedPartner.setPartnerId(partner.getPartnerId());
        convertedPartner.setUsername(partner.getUsername());
        convertedPartner.setPassword(partner.getPassword());
        return convertedPartner;
    }

    private ws.roomrate.RoomType convertToRoomRateRoomType(ws.roomtype.RoomType roomType) {
        if (roomType == null) {
            return null;
        }
        ws.roomrate.RoomType roomRateRoomType = new ws.roomrate.RoomType();
        roomRateRoomType.setRoomTypeId(roomType.getRoomTypeId());
        roomRateRoomType.setTypeName(roomType.getTypeName());
        roomRateRoomType.setSize(roomType.getSize());
        roomRateRoomType.setBed(roomType.getBed());
        roomRateRoomType.setDescription(roomType.getDescription());
        roomRateRoomType.setCapacity(roomType.getCapacity());
        roomRateRoomType.setAmenities(roomType.getAmenities());
        roomRateRoomType.setRoomTypeStatus(convertToRoomRateRoomTypeStatus(roomType.getRoomTypeStatus())); 
        roomRateRoomType.setTierNumber(roomType.getTierNumber());
        return roomRateRoomType;
    }

    private ws.roomrate.RoomTypeStatusEnum convertToRoomRateRoomTypeStatus(ws.roomtype.RoomTypeStatusEnum roomTypeStatus) {
        if (roomTypeStatus == null) {
            return null;
        }
        switch (roomTypeStatus) {
            case ACTIVE:
                return ws.roomrate.RoomTypeStatusEnum.ACTIVE;
            case DISABLED:
                return ws.roomrate.RoomTypeStatusEnum.DISABLED;
            default:
                throw new IllegalArgumentException("Unknown RoomTypeStatusEnum: " + roomTypeStatus);
        }
    }

    private ws.roomrate.ReservationTypeEnum convertToRoomRateReservationTypeEnum(ws.roomtype.ReservationTypeEnum reservationType) {
        if (reservationType == null) {
            return null;
        }
        switch (reservationType) {
            case ONLINE:
                return ws.roomrate.ReservationTypeEnum.ONLINE;
            case WALKIN:
                return ws.roomrate.ReservationTypeEnum.WALKIN;
            default:
                throw new IllegalArgumentException("Unknown ReservationTypeEnum: " + reservationType);
        }
    }

    private ws.roomratereservation.RoomRate convertToRoomRateReservationRoomRate(ws.roomrate.RoomRate roomRate) {
        if (roomRate == null) {
            return null;
        }
        ws.roomratereservation.RoomRate roomRateReservation = new ws.roomratereservation.RoomRate();
        roomRateReservation.setRoomRateId(roomRate.getRoomRateId());
        roomRateReservation.setRateName(roomRate.getRateName());
        roomRateReservation.setRateType(convertToRoomRateReservationRoomRateTypeEnum(roomRate.getRateType()));
        roomRateReservation.setRatePerNight(roomRate.getRatePerNight());
        roomRateReservation.setRoomRateStatus(convertToRoomRateReservationRoomRateStatusEnum(roomRate.getRoomRateStatus()));
        roomRateReservation.setPromotionStartDate(roomRate.getPromotionStartDate());
        roomRateReservation.setPromotionEndDate(roomRate.getPromotionEndDate());
        roomRateReservation.setPeakStartDate(roomRate.getPeakStartDate());
        roomRateReservation.setPeakEndDate(roomRate.getPeakEndDate());
        roomRateReservation.setRoomType(convertToRoomRateReservationRoomType(roomRate.getRoomType()));

        return roomRateReservation;
    }
    
    private ws.roomratereservation.RoomRateStatusEnum convertToRoomRateReservationRoomRateStatusEnum(ws.roomrate.RoomRateStatusEnum roomRateStatus) {
        if (roomRateStatus == null) {
            return null;
        }
        switch (roomRateStatus) {
            case ACTIVE:
                return ws.roomratereservation.RoomRateStatusEnum.ACTIVE;
            case DISABLED:
                return ws.roomratereservation.RoomRateStatusEnum.DISABLED;
            case NOT_AVAILABLE:
                return ws.roomratereservation.RoomRateStatusEnum.NOT_AVAILABLE;
            default:
                throw new IllegalArgumentException("Unknown RoomRateStatusEnum: " + roomRateStatus);
        }
    }

    private ws.roomratereservation.RoomRateTypeEnum convertToRoomRateReservationRoomRateTypeEnum(ws.roomrate.RoomRateTypeEnum rateType) {
        if (rateType == null) {
            return null;
        }
        switch (rateType) {
            case PUBLISHED:
                return ws.roomratereservation.RoomRateTypeEnum.PUBLISHED;
            case NORMAL:
                return ws.roomratereservation.RoomRateTypeEnum.NORMAL;
            case PEAK:
                return ws.roomratereservation.RoomRateTypeEnum.PEAK;
            case PROMOTION:
                return ws.roomratereservation.RoomRateTypeEnum.PROMOTION;
            default:
                throw new IllegalArgumentException("Unknown RoomRateTypeEnum: " + rateType);
        }
    }

    private ws.roomratereservation.RoomType convertToRoomRateReservationRoomType(ws.roomrate.RoomType roomType) {
        if (roomType == null) {
            return null;
        }
        ws.roomratereservation.RoomType roomTypeReservation = new ws.roomratereservation.RoomType();
        roomTypeReservation.setRoomTypeId(roomType.getRoomTypeId());
        roomTypeReservation.setTypeName(roomType.getTypeName());
        roomTypeReservation.setSize(roomType.getSize());
        roomTypeReservation.setBed(roomType.getBed());
        roomTypeReservation.setDescription(roomType.getDescription());
        roomTypeReservation.setCapacity(roomType.getCapacity());
        roomTypeReservation.setAmenities(roomType.getAmenities());
        roomTypeReservation.setTierNumber(roomType.getTierNumber());
        roomTypeReservation.setRoomTypeStatus(convertToRoomRateReservationRoomTypeStatus(roomType.getRoomTypeStatus()));

        return roomTypeReservation;
    }

    private ws.roomratereservation.RoomTypeStatusEnum convertToRoomRateReservationRoomTypeStatus(ws.roomrate.RoomTypeStatusEnum roomTypeStatus) {
        if (roomTypeStatus == null) {
            return null;
        }
        switch (roomTypeStatus) {
            case ACTIVE:
                return ws.roomratereservation.RoomTypeStatusEnum.ACTIVE;
            case DISABLED:
                return ws.roomratereservation.RoomTypeStatusEnum.DISABLED;
            default:
                throw new IllegalArgumentException("Unknown RoomTypeStatusEnum: " + roomTypeStatus);
        }
    }

    private ws.roomratereservation.Reservation convertToRoomRateReservation(ws.reservation.Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        ws.roomratereservation.Reservation reservationForRoomRate = new ws.roomratereservation.Reservation();
        reservationForRoomRate.setReservationId(reservation.getReservationId());
        reservationForRoomRate.setCheckInDate(reservation.getCheckInDate());
        reservationForRoomRate.setCheckOutDate(reservation.getCheckOutDate());
        reservationForRoomRate.setNumOfRooms(reservation.getNumOfRooms());
        reservationForRoomRate.setReservationType(convertToRoomRateReservationTypeEnum(reservation.getReservationType()));
        reservationForRoomRate.setReservationAmount(reservation.getReservationAmount());
        reservationForRoomRate.setReservationStatusEnum(convertToRoomRateReservationStatusEnum(reservation.getReservationStatusEnum()));
        reservationForRoomRate.setRoomType(convertReservationRoomTypeToRoomRateReservationRoomType(reservation.getRoomType()));

        return reservationForRoomRate;
    }

    private ws.roomratereservation.ReservationTypeEnum convertToRoomRateReservationTypeEnum(ws.reservation.ReservationTypeEnum reservationType) {
        if (reservationType == null) {
            return null;
        }
        switch (reservationType) {
            case ONLINE:
                return ws.roomratereservation.ReservationTypeEnum.ONLINE;
            case WALKIN:
                return ws.roomratereservation.ReservationTypeEnum.WALKIN;
            default:
                throw new IllegalArgumentException("Unknown ReservationTypeEnum: " + reservationType);
        }
    }

    private ws.roomratereservation.ReservationStatusEnum convertToRoomRateReservationStatusEnum(ws.reservation.ReservationStatusEnum reservationStatus) {
        if (reservationStatus == null) {
            return null;
        }
        switch (reservationStatus) {
            case CONFIRMED:
                return ws.roomratereservation.ReservationStatusEnum.CONFIRMED;
            case CHECKED_IN:
                return ws.roomratereservation.ReservationStatusEnum.CHECKED_IN;
            case CHECKED_OUT:
                return ws.roomratereservation.ReservationStatusEnum.CHECKED_OUT;
            default:
                throw new IllegalArgumentException("Unknown ReservationStatusEnum: " + reservationStatus);
        }
    }

    private ws.roomratereservation.RoomType convertReservationRoomTypeToRoomRateReservationRoomType(ws.reservation.RoomType reservationRoomType) {
        if (reservationRoomType == null) {
            return null;
        }
        ws.roomratereservation.RoomType roomRateReservationRoomType = new ws.roomratereservation.RoomType();
        roomRateReservationRoomType.setRoomTypeId(reservationRoomType.getRoomTypeId());
        roomRateReservationRoomType.setTypeName(reservationRoomType.getTypeName());
        roomRateReservationRoomType.setSize(reservationRoomType.getSize());
        roomRateReservationRoomType.setBed(reservationRoomType.getBed());
        roomRateReservationRoomType.setCapacity(reservationRoomType.getCapacity());
        roomRateReservationRoomType.setAmenities(reservationRoomType.getAmenities());
        roomRateReservationRoomType.setDescription(reservationRoomType.getDescription());
        roomRateReservationRoomType.setTierNumber(reservationRoomType.getTierNumber());
        roomRateReservationRoomType.setRoomTypeStatus(convertToRoomRateReservationRoomTypeStatus(reservationRoomType.getRoomTypeStatus()));

        return roomRateReservationRoomType;
    }

    private ws.roomratereservation.RoomTypeStatusEnum convertToRoomRateReservationRoomTypeStatus(ws.reservation.RoomTypeStatusEnum roomTypeStatus) {
        if (roomTypeStatus == null) {
            return null;
        }
        switch (roomTypeStatus) {
            case ACTIVE:
                return ws.roomratereservation.RoomTypeStatusEnum.ACTIVE;
            case DISABLED:
                return ws.roomratereservation.RoomTypeStatusEnum.DISABLED;
            default:
                throw new IllegalArgumentException("Unknown RoomTypeStatusEnum: " + roomTypeStatus);
        }
    }
    
    private String formatDate(Date date) {
        return date != null ? new SimpleDateFormat("dd-MM-yyyy").format(date) : "N/A";
    }

}
