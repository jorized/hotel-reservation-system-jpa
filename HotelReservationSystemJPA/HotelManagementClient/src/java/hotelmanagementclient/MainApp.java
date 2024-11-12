/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagementclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ExceptionReportSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomReservationSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Customer;
import entity.Employee;
import entity.ExceptionReport;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomReservation;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RoomStatusEnum;
import util.enumeration.RoomTypeStatusEnum;
import util.enumeration.RoomRateTypeEnum;
import util.exception.InvalidAccountCredentialsException;
import util.exception.InvalidRoomDetailsException;
import util.exception.InvalidRoomRateException;
import util.exception.InvalidRoomTypeCapacityException;
import util.exception.InvalidRoomTypeDetailsException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import util.enumeration.ExceptionTypeReportEnum;
import util.enumeration.ReservationStatusEnum;
import util.enumeration.ReservationTypeEnum;

/**
 *
 * @author JorJo
 */
public class MainApp {

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private ExceptionReportSessionBeanRemote exceptionReportSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private RoomReservationSessionBeanRemote roomReservationSessionBeanRemote;

    private Employee currentEmployee;

    public MainApp() {
        currentEmployee = null;
    }

    public MainApp(
            EmployeeSessionBeanRemote employeeSessionBeanRemote,
            PartnerSessionBeanRemote partnerSessionBeanRemote,
            RoomTypeSessionBeanRemote roomTypeSessionBeanRemote,
            RoomSessionBeanRemote roomSessionBeanRemote,
            ExceptionReportSessionBeanRemote exceptionReportSessionBeanRemote,
            RoomRateSessionBeanRemote roomRateSessionBeanRemote,
            ReservationSessionBeanRemote reservationSessionBeanRemote,
            GuestSessionBeanRemote guestSessionBeanRemote,
            CustomerSessionBeanRemote customerSessionBeanRemote,
            RoomReservationSessionBeanRemote roomReservationSessionBeanRemote
    ) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.exceptionReportSessionBeanRemote = exceptionReportSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.roomReservationSessionBeanRemote = roomReservationSessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to the HoRS Management Client ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("Enter your choice > ");
                response = scanner.nextInt();

                if (response == 1) {
                    doEmployeeLogin();
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Invalid option. Please try again. \n");
                }

            }

            if (response == 2) {
                break;
            }
        }
    }

    private void doEmployeeLogin() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Login ***\n");

            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            if (username.length() > 0 && password.length() > 0) {
                currentEmployee = employeeSessionBeanRemote.employeeLogin(username, password);
                doAfterEmployeeLogin();
            } else {
                throw new InvalidAccountCredentialsException("Invalid account credentials");
            }
        } catch (Exception ex) {
            System.out.println("Error logging in: " + ex.getMessage() + "\n");
        }
    }

    private void doAfterEmployeeLogin() {
        try {

            EmployeeAccessRightEnum currentRole = currentEmployee.getRole();

            //If Sysadmin
            if (currentRole == EmployeeAccessRightEnum.SYSTEM_ADMINISTRATOR) {
                showSysAdminMenu();
            } else if (currentRole == EmployeeAccessRightEnum.OPERATION_MANAGER) { //If Ops manager
                showOpsManagerMenu();
            } else if (currentRole == EmployeeAccessRightEnum.SALES_MANAGER) { //If Sales manager
                showSalesManagerMenu();
            } else { //If Guest relations officer
                showGuestRelationOfficerMenu();
            }
        } catch (Exception ex) {
            System.out.println("Error performing employee actions: " + ex.getMessage() + "\n");
        }
    }

    /**
     * ALL SYSTEM ADMINISTRATOR ACTIONS *
     */
    private void showSysAdminMenu() {
        try {

            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                //This is to check if user initiated the logout command
                if (currentEmployee == null) {
                    break;
                }

                System.out.println("*** HoRS Management Client ::  Welcome, " + currentEmployee.getUsername() + ". What can i do for you today?***\n");
                System.out.println("1: Manage employees");
                System.out.println("2: Manage partners");
                System.out.println("3: Logout\n");
                response = 0;

                while (response < 1 || response > 3) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doManageEmployees();
                    } else if (response == 2) {
                        doManagePartners();
                    } else if (response == 3) { //Since before this is the first screen, can just do a break
                        System.out.println("Logging out...\n");
                        currentEmployee = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }

                if (response == 3) {
                    break;
                }

            }

        } catch (Exception ex) {
            System.out.println("Error showing sysadmin menu: " + ex.getMessage() + "\n");
        }

    }

    private void doManageEmployees() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                if (currentEmployee == null) {
                    break;
                }

                System.out.println("*** HoRS Management Client :: Manage employees ***\n");
                System.out.println("1: Create new employee");
                System.out.println("2: View all employees");
                System.out.println("3: Go back");
                System.out.println("4: Logout\n");
                response = 0;

                while (response < 1 || response > 4) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doCreateNewEmployee();
                    } else if (response == 2) {
                        doViewAllEmployees();
                    } else if (response == 3) {
                        break;
                    } else if (response == 4) {
                        System.out.println("Logging out...\n");
                        currentEmployee = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }

                if (response == 3 || response == 4) {
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error performing manage employees: " + ex.getMessage() + "\n");
        }
    }

    private void doCreateNewEmployee() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Create new employee ***\n");

            System.out.print("Enter employee username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter employee password: ");
            String password = scanner.nextLine().trim();

            System.out.println("Select employee role: ");
            EmployeeAccessRightEnum[] roles = EmployeeAccessRightEnum.values();
            for (int i = 0; i < roles.length; i++) {
                System.out.println((i + 1) + ") " + formatEnumString(roles[i].toString()));
            }

            int response = 0;
            while (true) {
                System.out.print("Enter your choice > ");
                response = scanner.nextInt();

                if (response >= 1 && response <= roles.length) {
                    EmployeeAccessRightEnum role = roles[response - 1];
                    scanner.nextLine();

                    if (username.length() > 0 && password.length() > 0) {
                        Employee newEmployee = new Employee(username, password, role);
                        employeeSessionBeanRemote.createNewEmployee(newEmployee);
                        System.out.println("Employee has successfully been created!\n");
                    } else {
                        throw new InvalidAccountCredentialsException("Invalid account credentials");
                    }

                    break;
                } else {
                    System.out.println("Invalid option. Please try again.\n");
                }
            }

        } catch (Exception ex) {
            System.out.println("Error creating new employee: " + ex.getMessage() + "\n");
        }
    }

    private void doViewAllEmployees() {
        try {
            System.out.println("\n*** HoRS Management Client :: View All Employees ***\n");

            // Print header with consistent column widths
            System.out.printf("%-5s %-20s %-20s\n", "No.", "Username", "Role");
            System.out.println("---------------------------------------------------");

            List<Employee> employees = employeeSessionBeanRemote.retrieveAllEmployees();

            if (employees.isEmpty()) {
                System.out.println("No employees found in the system.\n");
            } else {
                int index = 1;
                for (Employee employee : employees) {
                    System.out.printf("%-5d %-20s %-20s\n",
                            index++,
                            employee.getUsername(),
                            formatEnumString(employee.getRole().toString()));
                }
                System.out.println("---------------------------------------------------\n");
            }

        } catch (Exception ex) {
            System.out.println("Error viewing all employees: " + ex.getMessage() + "\n");
        }
    }

    private void doManagePartners() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                if (currentEmployee == null) {
                    break;
                }

                System.out.println("*** HoRS Management Client :: Manage partners ***\n");
                System.out.println("1: Create new partner");
                System.out.println("2: View all partners");
                System.out.println("3: Go back");
                System.out.println("4: Logout\n");
                response = 0;

                while (response < 1 || response > 4) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doCreateNewPartner();
                    } else if (response == 2) {
                        doViewAllPartners();
                    } else if (response == 3) {
                        break;
                    } else if (response == 4) {
                        System.out.println("Logging out...\n");
                        currentEmployee = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }

                if (response == 3 || response == 4) {
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error performing manage partners: " + ex.getMessage() + "\n");
        }
    }

    private void doCreateNewPartner() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Create new partner ***\n");

            System.out.print("Enter partner username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter partner password: ");
            String password = scanner.nextLine().trim();

            if (username.length() > 0 && password.length() > 0) {
                Partner newPartner = new Partner(username, password);
                partnerSessionBeanRemote.createNewPartner(newPartner);
                System.out.println("Partner has successfully been created!\n");
            } else {
                throw new InvalidAccountCredentialsException("Invalid account credentials");
            }

        } catch (Exception ex) {
            System.out.println("Error creating new employee: " + ex.getMessage() + "\n");
        }
    }

    private void doViewAllPartners() {
        try {
            System.out.println("\n*** HoRS Management Client :: View All Partners ***\n");

            List<Partner> partners = partnerSessionBeanRemote.retrieveAllPartners();

            if (partners.isEmpty()) {
                System.out.println("No partners found in the system.\n");
            } else {
                System.out.printf("%-5s %-20s\n", "No.", "Username");
                System.out.println("---------------------");

                int index = 1;
                for (Partner partner : partners) {
                    System.out.printf("%-5d %-20s\n",
                            index++,
                            partner.getUsername());
                }
                System.out.println("---------------------\n");
            }

        } catch (Exception ex) {
            System.out.println("Error viewing all partners: " + ex.getMessage() + "\n");
        }
    }

    /**
     * ALL OPERATION MANAGER METHODS *
     */
    private void showOpsManagerMenu() {
        try {

            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                //This is to check if user initiated the logout command
                if (currentEmployee == null) {
                    break;
                }

                System.out.println("*** HoRS Management Client ::  Welcome, " + currentEmployee.getUsername() + ". What can i do for you today?***\n");
                System.out.println("1: Manage room types");
                System.out.println("2: Manage rooms");
                System.out.println("3: View room allocation exception report");
                System.out.println("4: Trigger room allocation for a specific date");
                System.out.println("5: Logout\n");
                response = 0;

                while (response < 1 || response > 5) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doManageRoomTypes();
                    } else if (response == 2) {
                        doManageRooms();
                    } else if (response == 3) {
                        doViewRoomAllocationExceptionReport();
                    } else if (response == 4) {
                        doTriggerRoomAllocationForSpecificDate();
                    } else if (response == 5) { //Since before this is the first screen, can just do a break
                        System.out.println("Logging out...\n");
                        currentEmployee = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }

                if (response == 3) {
                    break;
                }

            }

        } catch (Exception ex) {
            System.out.println("Error showing sysadmin menu: " + ex.getMessage() + "\n");
        }

    }

    private void doManageRoomTypes() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                if (currentEmployee == null) {
                    break;
                }

                System.out.println("*** HoRS Management Client :: Manage room types ***\n");
                System.out.println("1: Create new room type");
                System.out.println("2: View room type details");
                System.out.println("3: Update room type");
                System.out.println("4: Delete room type");
                System.out.println("5: View all room types");
                System.out.println("6: Go back");
                System.out.println("7: Logout\n");
                response = 0;

                while (response < 1 || response > 7) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doCreateNewRoomType();
                    } else if (response == 2) {
                        doViewRoomTypeDetails();
                    } else if (response == 3) {
                        doUpdateRoomType();
                    } else if (response == 4) {
                        doDeleteRoomType();
                    } else if (response == 5) {
                        doViewAllRoomTypes();
                    } else if (response == 6) {
                        break;
                    } else if (response == 7) {
                        System.out.println("Logging out...\n");
                        currentEmployee = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }

                if (response == 6 || response == 7) {
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error performing manage room types: " + ex.getMessage() + "\n");
        }
    }

    private void doCreateNewRoomType() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Create new room type ***\n");

            System.out.print("Enter room type name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter room type size: ");
            String size = scanner.nextLine().trim();

            System.out.print("Enter room type bed: ");
            String bed = scanner.nextLine().trim();

            System.out.print("Enter room type description: ");
            String description = scanner.nextLine().trim();

            System.out.print("Enter room type capacity: ");
            String capacity = scanner.nextLine().trim();

            System.out.print("Enter room type amenities: ");
            String amenities = scanner.nextLine().trim();

            System.out.print("Enter room type tier number: ");
            String tierNumber = scanner.nextLine().trim();

            //If all inputs are filled in
            if (name.length() > 0 && size.length() > 0 && bed.length() > 0 && description.length() > 0
                    && capacity.length() > 0 && amenities.length() > 0 && tierNumber.length() > 0) {
                //If capacity is more than 0
                if (Integer.parseInt(capacity) > 0) {
                    RoomType newRoomType = new RoomType(name, size, bed, description, Integer.parseInt(capacity), amenities, Integer.parseInt(tierNumber));
                    roomTypeSessionBeanRemote.createNewRoomType(newRoomType);
                } else {
                    throw new InvalidRoomTypeCapacityException("Invalid room type capacity");
                }

            } else {
                throw new InvalidRoomTypeDetailsException("Invalid room type details");
            }

            System.out.println("\nNew room type '" + name + "' has successfully been created.\n");

        } catch (Exception ex) {
            System.out.println("Error creating new room type: " + ex.getMessage() + "\n");
        }
    }

    private void doViewRoomTypeDetails() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: View room type details ***\n");

            displayRoomTypes();

            System.out.print("\nEnter room type name: ");
            String name = scanner.nextLine().trim();

            RoomType roomType = roomTypeSessionBeanRemote.retrieveRoomTypeByName(name);

            System.out.println("\nRoom type found. Details for room type '" + roomType.getTypeName() + "': ");
            System.out.println("Size: " + roomType.getSize());
            System.out.println("Bed: " + roomType.getBed());
            System.out.println("Description: " + roomType.getDescription());
            System.out.println("Total capacity: " + roomType.getCapacity());
            System.out.println("Number of available rooms left: ");
            System.out.println("Amenities: " + roomType.getAmenities());
            System.out.println("Tier number: " + roomType.getTierNumber());
            System.out.println("Status: " + formatEnumString(roomType.getRoomTypeStatus().toString()) + "\n");

        } catch (Exception ex) {
            System.out.println("Error viewing room type: " + ex.getMessage() + "\n");
        }
    }

    private void doUpdateRoomType() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Update room type ***\n");

            displayRoomTypes();

            System.out.print("\nEnter room type name: ");
            String name = scanner.nextLine().trim();

            RoomType existingRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByName(name);

            System.out.println("\nRoom type found. Details for room type '" + existingRoomType.getTypeName() + "': ");
            System.out.println("Size: " + existingRoomType.getSize());
            System.out.println("Bed: " + existingRoomType.getBed());
            System.out.println("Description: " + existingRoomType.getDescription());
            System.out.println("Total capacity: " + existingRoomType.getCapacity());
            System.out.println("Number of available rooms left: ");
            System.out.println("Amenities: " + existingRoomType.getAmenities());
            System.out.println("Tier number: " + existingRoomType.getTierNumber());
            System.out.println("Status: " + formatEnumString(existingRoomType.getRoomTypeStatus().toString()));

            RoomType updatedRoomType = new RoomType();
            updatedRoomType.setRoomTypeId(existingRoomType.getRoomTypeId());

            System.out.print("\nEnter new size (or press Enter to keep current): ");
            String updatedSize = scanner.nextLine().trim();
            if (!updatedSize.isEmpty()) {
                updatedRoomType.setSize(updatedSize);
            }

            System.out.print("Enter new bed (or press Enter to keep current): ");
            String updatedBed = scanner.nextLine().trim();
            if (!updatedBed.isEmpty()) {
                updatedRoomType.setBed(updatedBed);
            }

            System.out.print("Enter new description (or press Enter to keep current): ");
            String updatedDescription = scanner.nextLine().trim();
            if (!updatedDescription.isEmpty()) {
                updatedRoomType.setDescription(updatedDescription);
            }

            System.out.print("Enter new capacity (or press Enter to keep current): ");
            String updatedCapacity = scanner.nextLine().trim();
            if (!updatedCapacity.isEmpty()) {
                updatedRoomType.setCapacity(Integer.parseInt(updatedCapacity));
            }

            System.out.print("Enter new amenities (or press Enter to keep current): ");
            String updatedAmenities = scanner.nextLine().trim();
            if (!updatedAmenities.isEmpty()) {
                updatedRoomType.setAmenities(updatedAmenities);
            }

            System.out.print("Enter new tier number (or press Enter to keep current): ");
            String updatedTierNumber = scanner.nextLine().trim();
            if (!updatedTierNumber.isEmpty()) {
                updatedRoomType.setTierNumber(Integer.parseInt(updatedTierNumber));
            }

            System.out.print("Enter new status (or press Enter to keep current): ");
            String updatedStatus = scanner.nextLine().trim();
            if (!updatedStatus.isEmpty()) {
                updatedRoomType.setRoomTypeStatus(RoomTypeStatusEnum.valueOf(updatedStatus.toUpperCase()));
            }

            roomTypeSessionBeanRemote.updateRoomType(updatedRoomType);

            System.out.println("\nRoom type updated successfully.");

        } catch (Exception ex) {
            System.out.println("Error updating room type: " + ex.getMessage() + "\n");
        }
    }

    private void doDeleteRoomType() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Delete room type ***\n");

            displayRoomTypes();

            System.out.print("\nEnter room type name: ");
            String name = scanner.nextLine().trim();

            RoomType existingRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByName(name);

            System.out.println("\nRoom type found. Details for room type '" + existingRoomType.getTypeName() + "': ");
            System.out.println("Size: " + existingRoomType.getSize());
            System.out.println("Bed: " + existingRoomType.getBed());
            System.out.println("Description: " + existingRoomType.getDescription());
            System.out.println("Total capacity: " + existingRoomType.getCapacity());
            System.out.println("Number of available rooms left: ");
            System.out.println("Amenities: " + existingRoomType.getAmenities());
            System.out.println("Tier number: " + existingRoomType.getTierNumber());
            System.out.println("Status: " + formatEnumString(existingRoomType.getRoomTypeStatus().toString()));

            System.out.print("\nAre you sure you want to delete this room type? (Y/N): ");
            String response = scanner.nextLine().trim();

            if (response.toLowerCase().equals("y")) {
                roomTypeSessionBeanRemote.deleteRoomType(existingRoomType);
                System.out.println("Room type has successfully been removed.\n");
            } else if (response.toLowerCase().equals("n")) {
                System.out.println("\n");
            } else {
                System.out.println("Invalid option. Please try again. \n");
            }

        } catch (Exception ex) {
            System.out.println("Error deleting room type: " + ex.getMessage() + "\n");
        }
    }

    //Showing only the relevant fields, because won't look nice on CLI
    private void doViewAllRoomTypes() {
        try {
            System.out.println("\n*** HoRS Management Client :: View All Room Types ***\n");

            // Print header with a consistent width for each column
            System.out.printf("%-5s %-20s %-15s %-15s %-15s\n",
                    "No.", "Name", "Capacity", "Tier", "Status");
            System.out.println("---------------------------------------------------------------------");

            List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();
            if (roomTypes.isEmpty()) {
                System.out.println("No room types found.\n");
            } else {
                int index = 1;
                for (RoomType roomType : roomTypes) {
                    System.out.printf("%-5d %-20s %-15d %-15d %-15s\n",
                            index++,
                            roomType.getTypeName(),
                            roomType.getCapacity(),
                            roomType.getTierNumber(),
                            formatEnumString(roomType.getRoomTypeStatus().toString()));
                }
                System.out.println("---------------------------------------------------------------------\n");
            }

        } catch (Exception ex) {
            System.out.println("Error viewing all room types: " + ex.getMessage() + "\n");
        }
    }

    private void doManageRooms() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                if (currentEmployee == null) {
                    break;
                }

                System.out.println("*** HoRS Management Client :: Manage room ***\n");
                System.out.println("1: Create new room");
                System.out.println("2: Update room");
                System.out.println("3: Delete room");
                System.out.println("4: View all rooms");
                System.out.println("5: Go back");
                System.out.println("6: Logout\n");
                response = 0;

                while (response < 1 || response > 6) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doCreateNewRoom();
                    } else if (response == 2) {
                        doUpdateRoom();
                    } else if (response == 3) {
                        doDeleteRoom();
                    } else if (response == 4) {
                        doViewAllRooms();
                    } else if (response == 5) {
                        break;
                    } else if (response == 6) {
                        System.out.println("Logging out...\n");
                        currentEmployee = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }

                if (response == 5 || response == 6) {
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error performing manage rooms: " + ex.getMessage() + "\n");
        }
    }

    private void doCreateNewRoom() throws InvalidRoomDetailsException {
        try {

            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Create new room ***\n");

            displayActiveRoomTypes();

            System.out.print("\nEnter room type: ");
            String roomTypeString = scanner.nextLine().trim();

            RoomType existingRoomType = roomTypeSessionBeanRemote.retrieveActiveRoomTypeByName(roomTypeString);

            String floorNum;
            String seqNum;

            // Loop until valid 2-digit floor number is entered
            while (true) {
                System.out.print("Enter floor number (2 digits): ");
                floorNum = scanner.nextLine().trim();
                if (floorNum.matches("\\d{2}")) { // Check if it's exactly 2 digits
                    break;
                } else {
                    System.out.println("Invalid input. Floor number must be exactly 2 digits.");
                }
            }

            // Loop until valid 2-digit sequence number is entered
            while (true) {
                System.out.print("Enter sequence number (2 digits): ");
                seqNum = scanner.nextLine().trim();
                if (seqNum.matches("\\d{2}")) { // Check if it's exactly 2 digits
                    break;
                } else {
                    System.out.println("Invalid input. Sequence number must be exactly 2 digits.");
                }
            }

            // catch invalid details
            if (floorNum.isEmpty() || seqNum.isEmpty() || existingRoomType == null) {
                throw new InvalidRoomDetailsException("Invalid room details");
            }

            String roomNum = floorNum + seqNum;
            if (roomSessionBeanRemote.checkRoomNum(roomNum)) {
                System.out.println("\nA room with room number " + roomNum + " already exists. Room creation aborted.");
                return;
            }

            // Create and save the room if all inputs are valid
            Room newRoom = new Room(floorNum, seqNum, existingRoomType);
            roomSessionBeanRemote.createNewRoom(newRoom);

            System.out.println("\nNew room for '" + roomTypeString + "' has successfully been created.\n");
        } catch (InvalidRoomDetailsException ex) {
            System.out.println("Error creating new room: Invalid room details.");
        } catch (Exception ex) {
            System.out.println("Error creating new room: " + ex.getMessage() + "\n");
        }
    }

    private void doUpdateRoom() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Update room ***\n");

            displayRoomNumbers();

            System.out.print("\nEnter room number: ");
            String roomNumString = scanner.nextLine().trim();

            Room existingRoom = roomSessionBeanRemote.retrieveRoomByRoomNum(roomNumString);

            System.out.println("\nRoom Number found. Details for room number '" + existingRoom.getRoomNum() + "': ");
            System.out.println("Floor Number: " + existingRoom.getFloorNum());
            System.out.println("Sequence Number: " + existingRoom.getSequenceNum());
            System.out.println("Room Number: " + existingRoom.getRoomNum());
            System.out.println("Room Status: " + existingRoom.getRoomStatus());

            Room updatedRoom = new Room();
            updatedRoom.setRoomId(existingRoom.getRoomId());

            // not sure if changing the floor num and seq num makes sense anot...
            // Loop to get valid 2-digit floor number
            String finalFloorNum;
            while (true) {

                System.out.print("\nEnter new floor number (2 digits, or press Enter to keep current): ");
                String updatedFloorNum = scanner.nextLine().trim();

                if (updatedFloorNum.isEmpty()) {
                    finalFloorNum = existingRoom.getFloorNum(); // Use the existing floor number if no input
                    break;
                } else if (updatedFloorNum.matches("\\d{2}")) { // Check if it's exactly 2 digits
                    finalFloorNum = updatedFloorNum;
                    break;
                } else {
                    System.out.println("Invalid input. Floor number must be exactly 2 digits.");
                }
            }

            String finalSeqNum;
            while (true) {
                System.out.print("Enter new sequence number (2 digits, or press Enter to keep current): ");
                String updatedSeqNum = scanner.nextLine().trim();
                if (updatedSeqNum.isEmpty()) {
                    finalSeqNum = existingRoom.getSequenceNum(); // Use the existing sequence number if no input
                    break;
                } else if (updatedSeqNum.matches("\\d{2}")) { // Check if it's exactly 2 digits
                    finalSeqNum = updatedSeqNum;
                    break;
                } else {
                    System.out.println("Invalid input. Sequence number must be exactly 2 digits.");
                }
            }

            // join to get roomNum
            String roomNum = finalFloorNum + finalSeqNum;
            if (roomSessionBeanRemote.checkRoomNum(roomNum)) {
                System.out.println("\nA room with room number " + roomNum + " already exists. Room Update aborted.\n");
                return;
            }
            updatedRoom.setFloorNum(finalFloorNum);
            updatedRoom.setSequenceNum(finalSeqNum);
            updatedRoom.setRoomNum(roomNum);

            System.out.print("Enter new status (or press Enter to keep current): ");
            String updatedStatus = scanner.nextLine().trim();
            if (!updatedStatus.isEmpty()) {
                updatedRoom.setRoomStatus(RoomStatusEnum.valueOf(updatedStatus.toUpperCase()));
            }

            roomSessionBeanRemote.updateRoom(updatedRoom);

            System.out.println("\nRoom updated successfully.");

        } catch (Exception ex) {
            System.out.println("Error updating room: " + ex.getMessage() + "\n");
        }
    }

    private void doDeleteRoom() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Delete room ***\n");

            displayRoomNumbers();

            System.out.print("\nEnter room number: ");
            String roomNumString = scanner.nextLine().trim();

            Room existingRoom = roomSessionBeanRemote.retrieveRoomByRoomNum(roomNumString);

            System.out.println("\nRoom type found. Details for room number: " + existingRoom.getRoomNum() + ": ");
            System.out.println("Floor Number: " + existingRoom.getFloorNum());
            System.out.println("Sequence Number: " + existingRoom.getSequenceNum());
            System.out.println("Room Number: " + existingRoom.getRoomNum());
            System.out.println("Status: " + formatEnumString(existingRoom.getRoomStatus().toString()));

            System.out.print("\nAre you sure you want to delete this room? (Y/N): ");
            String response = scanner.nextLine().trim();

            if (response.toLowerCase().equals("y")) {
                String resultMessage = roomSessionBeanRemote.deleteRoom(existingRoom);
                System.out.println(resultMessage);
            } else if (response.toLowerCase().equals("n")) {
                System.out.println("\n");
            } else {
                System.out.println("Invalid option. Please try again. \n");
            }

        } catch (Exception ex) {
            System.out.println("Error deleting room: " + ex.getMessage() + "\n");
        }
    }

    private void doViewAllRooms() {
        try {
            System.out.println("\n*** HoRS Management Client :: View All Rooms ***\n");

            System.out.printf("%-5s %-15s %-15s %-20s %-20s %-15s\n",
                    "No.", "Room Number", "Floor Number", "Sequence Number", "Room Type", "Status");
            System.out.println("--------------------------------------------------------------------------------------------");

            List<Room> rooms = roomSessionBeanRemote.retrieveAllRooms();

            if (rooms.isEmpty()) {
                System.out.println("No rooms found in the system.\n");
            } else {
                int index = 1;
                for (Room room : rooms) {
                    System.out.printf("%-5d %-15s %-15s %-20s %-20s %-15s\n",
                            index++,
                            room.getRoomNum(),
                            room.getFloorNum(),
                            room.getSequenceNum(),
                            room.getRoomType().getTypeName(),
                            formatEnumString(room.getRoomStatus().toString()));
                }
                System.out.println("--------------------------------------------------------------------------------------------\n");
            }

        } catch (Exception ex) {
            System.out.println("Error viewing all rooms: " + ex.getMessage() + "\n");
        }
    }

    private void doViewRoomAllocationExceptionReport() {
        try {

            System.out.println("\n*** HoRS Management Client :: View Room Allocation Exception Report ***\n");

            List<ExceptionReport> exceptionReports = exceptionReportSessionBeanRemote.retrieveAllExceptionReports();

            // Check if there are any exception reports
            if (exceptionReports.isEmpty()) {
                System.out.println("No room allocation exceptions found. All reservations are allocated successfully.\n");
            } else {
                System.out.printf("%-5s %-20s %-20s %-20s\n", "No.", "Type", "Reservation No.", "Remarks");
                System.out.println("---------------------------------------------------------------");

                int index = 1;
                for (ExceptionReport exceptionReport : exceptionReports) {
                    System.out.printf("%-5d %-20s %-20s %-20s\n",
                            index++,
                            formatEnumString(exceptionReport.getExceptionTypeReport().toString()),
                            exceptionReport.getRoomReservation().getReservation().getReservationId(),
                            exceptionReport.getExceptionTypeReport() == ExceptionTypeReportEnum.TYPE_1
                            ? "Upgraded from " + exceptionReport.getRoomReservation().getReservation().getRoomType().getTypeName()
                            + " to " + exceptionReport.getRoomReservation().getRoom().getRoomType().getTypeName()
                            : "No upgrade to next higher tier"
                    );
                }
                System.out.println("---------------------------------------------------------------\n");
            }
        } catch (Exception ex) {
            System.out.println("Error viewing room allocation exception reports: " + ex.getMessage() + "\n");
        }
    }

    private void doTriggerRoomAllocationForSpecificDate() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Trigger room allocation for specific date ***\n");

            System.out.print("Enter check-in date (yyyy-MM-dd): ");
            String enteredDate = scanner.nextLine().trim();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date userDate = dateFormat.parse(enteredDate);

            // Use stripTime to remove time from current date and user-entered date
            Date currentDate = stripTime(new Date());
            userDate = stripTime(userDate);

            // Check if the entered date is in the past
            if (userDate.before(currentDate)) {
                System.out.println("The entered date is in the past. Please enter a future date.");
            } else {
                roomSessionBeanRemote.allocateRoomsManually(userDate);
                System.out.println("Room allocation triggered for check-in date: " + userDate);
            }

        } catch (Exception ex) {
            System.out.println("Error triggering room allocation for specific date: " + ex.getMessage() + "\n");
        }
    }

    /**
     * ALL SALES MANAGER METHODS *
     */
    private void showSalesManagerMenu() {
        try {

            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                //This is to check if user initiated the logout command
                if (currentEmployee == null) {
                    break;
                }

                System.out.println("*** HoRS Management Client ::  Welcome, " + currentEmployee.getUsername() + ". What can i do for you today?***\n");
                System.out.println("1: Create new room rate");
                System.out.println("2: View room rate details");
                System.out.println("3: Update room rate");
                System.out.println("4: Delete room rate");
                System.out.println("5: View all room rates");
                System.out.println("6: Logout\n");
                response = 0;

                while (response < 1 || response > 6) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doCreateNewRoomRate();
                    } else if (response == 2) {
                        doViewRoomRateDetails();
                    } else if (response == 3) {
                        doUpdateRoomRate();
                    } else if (response == 4) {
                        doDeleteRoomRate();
                    } else if (response == 5) {
                        doViewAllRoomRates();
                    } else if (response == 6) { //Since before this is the first screen, can just do a break
                        System.out.println("Logging out...\n");
                        currentEmployee = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }

                if (response == 6) {
                    break;
                }

            }

        } catch (Exception ex) {
            System.out.println("Error showing sales manager menu: " + ex.getMessage() + "\n");
        }

    }

    private void doCreateNewRoomRate() throws InvalidRoomRateException {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Create new room rate ***\n");

            displayRoomTypes();

            System.out.print("\nEnter room type: ");
            String roomTypeString = scanner.nextLine().trim();

            RoomType existingRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByName(roomTypeString);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            System.out.print("Enter room rate name: ");
            String name = scanner.nextLine().trim();

            RoomRateTypeEnum rateType = null;
            while (rateType == null) {
                System.out.print("Enter room rate type (PUBLISHED, NORMAL, PEAK, PROMOTION): ");
                String input = scanner.nextLine().trim().toUpperCase();

                try {
                    rateType = RoomRateTypeEnum.valueOf(input);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid room rate type. Please enter one of the following: PUBLISHED, NORMAL, PEAK, PROMOTION.");
                }
            }

            System.out.print("Enter rate per night: ");
            BigDecimal ratePerNight = new BigDecimal(scanner.nextLine().trim());

            Date promotionStartDate = null;
            Date promotionEndDate = null;
            Date peakStartDate = null;
            Date peakEndDate = null;

            if (rateType == RoomRateTypeEnum.PROMOTION) {
                promotionStartDate = promptForDate(scanner, dateFormat, "Enter promotion start date (yyyy-MM-dd): ");
                promotionEndDate = promptForDate(scanner, dateFormat, "Enter promotion end date (yyyy-MM-dd): ");
            } else if (rateType == RoomRateTypeEnum.PEAK) {
                peakStartDate = promptForDate(scanner, dateFormat, "Enter peak start date (yyyy-MM-dd): ");
                peakEndDate = promptForDate(scanner, dateFormat, "Enter peak end date (yyyy-MM-dd): ");
            }

            //If all inputs are filled in
            if (name.length() > 0 && ratePerNight.compareTo(BigDecimal.ZERO) > 0) {
                RoomRate newRoomRate = new RoomRate(name, rateType, ratePerNight, promotionStartDate, promotionEndDate, peakStartDate, peakEndDate, existingRoomType);
                roomRateSessionBeanRemote.createNewRoomRate(newRoomRate);
            } else {
                throw new InvalidRoomRateException("Invalid room rate details.");
            }

            System.out.println("\nNew room rate '" + name + "' has successfully been created.\n");

        } catch (Exception ex) {
            System.out.println("Error creating new room rate: " + ex.getMessage() + "\n");
        }

    }

    private void doViewRoomRateDetails() {
        try {

            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: View Room Rate Details ***\n");

            displayRoomNames();

            System.out.print("\nEnter room rate name: ");
            String rateNameString = scanner.nextLine().trim();

            RoomRate existingRoomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomName(rateNameString);

            System.out.println("\nRoom rate found. Details for room rate name: " + existingRoomRate.getRateName() + "");
            System.out.println("Room Type: " + existingRoomRate.getRoomType().getTypeName());
            System.out.println("Room Rate Type: " + formatEnumString(existingRoomRate.getRateType().toString()));

            // check if rate is PROMOTION or PEAK
            if (existingRoomRate.getRateType() == RoomRateTypeEnum.PROMOTION) {
                System.out.println("Promotion Start Date: " + existingRoomRate.getPromotionStartDate());
                System.out.println("Promotion End Date: " + existingRoomRate.getPromotionEndDate());
            } else if (existingRoomRate.getRateType() == RoomRateTypeEnum.PEAK) {
                System.out.println("Peak Start Date: " + existingRoomRate.getPeakStartDate());
                System.out.println("Peak End Date: " + existingRoomRate.getPeakEndDate());
            }

            System.out.println("Rate Per Night: $" + existingRoomRate.getRatePerNight() + "\n");

        } catch (Exception ex) {
            System.out.println("Error viewing room rate details: " + ex.getMessage() + "\n");
        }
    }

    private void doUpdateRoomRate() {
        try {
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            System.out.println("*** HoRS Management Client :: Update room rate ***\n");

            displayRoomNames();

            System.out.print("\nEnter room rate name: ");
            String rateNameString = scanner.nextLine().trim();

            RoomRate existingRoomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomName(rateNameString);

            System.out.println("\nRoom rate found. Details for room rate name: " + existingRoomRate.getRateName() + "");
            System.out.println("Room Type: " + existingRoomRate.getRoomType().getTypeName());
            System.out.println("Room Rate Type: " + formatEnumString(existingRoomRate.getRateType().toString()));

            // Check if rate is PROMOTION or PEAK
            if (existingRoomRate.getRateType() == RoomRateTypeEnum.PROMOTION) {
                System.out.println("Promotion Start Date: " + existingRoomRate.getPromotionStartDate());
                System.out.println("Promotion End Date: " + existingRoomRate.getPromotionEndDate());
            } else if (existingRoomRate.getRateType() == RoomRateTypeEnum.PEAK) {
                System.out.println("Peak Start Date: " + existingRoomRate.getPeakStartDate());
                System.out.println("Peak End Date: " + existingRoomRate.getPeakEndDate());
            }

            System.out.println("Rate Per Night: $" + existingRoomRate.getRatePerNight() + "\n");

            RoomRate updatedRoomRate = new RoomRate();
            updatedRoomRate.setRoomRateId(existingRoomRate.getRoomRateId());

            displayRoomTypes();
            
            System.out.print("\nEnter new room type (or press Enter to keep current): ");
            String updatedRoomType = scanner.nextLine().trim();
            if (!updatedRoomType.isEmpty()) {
                RoomType existingRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByName(updatedRoomType);
                updatedRoomRate.setRoomType(existingRoomType);
            }

            System.out.print("Enter new rate type (PROMOTION/PEAK/PUBLISHED/NORMAL) (or press Enter to keep current): ");
            String rateTypeInput = scanner.nextLine().trim();
            if (!rateTypeInput.isEmpty()) {
                try {
                    RoomRateTypeEnum newRateType = RoomRateTypeEnum.valueOf(rateTypeInput.toUpperCase());
                    updatedRoomRate.setRateType(newRateType);

                    if (newRateType == RoomRateTypeEnum.PROMOTION) {
                        Date promotionStartDate = promptForDate(scanner, dateFormat, "Enter promotion start date (yyyy-MM-dd): ");
                        Date promotionEndDate = promptForDate(scanner, dateFormat, "Enter promotion end date (yyyy-MM-dd): ");
                        updatedRoomRate.setPromotionStartDate(promotionStartDate);
                        updatedRoomRate.setPromotionEndDate(promotionEndDate);
                    } else if (newRateType == RoomRateTypeEnum.PEAK) {
                        Date peakStartDate = promptForDate(scanner, dateFormat, "Enter peak start date (yyyy-MM-dd): ");
                        Date peakEndDate = promptForDate(scanner, dateFormat, "Enter peak end date (yyyy-MM-dd): ");
                        updatedRoomRate.setPeakStartDate(peakStartDate);
                        updatedRoomRate.setPeakEndDate(peakEndDate);
                    } else {
                        updatedRoomRate.setPromotionStartDate(null);
                        updatedRoomRate.setPromotionEndDate(null);
                        updatedRoomRate.setPeakStartDate(null);
                        updatedRoomRate.setPeakEndDate(null);
                    }
                } catch (IllegalArgumentException ex) {
                    System.out.println("Invalid rate type. Keeping current value.");
                }
            }

            System.out.print("Enter new rate per night (or press Enter to keep current): ");
            String ratePerNightInput = scanner.nextLine().trim();
            if (!ratePerNightInput.isEmpty()) {
                try {
                    updatedRoomRate.setRatePerNight(new BigDecimal(ratePerNightInput));
                } catch (NumberFormatException ex) {
                    System.out.print("Invalid input for rate per night. Keeping current value.");
                }
            }

            roomRateSessionBeanRemote.updateRoomRate(updatedRoomRate);
            System.out.println("\nRoom rate updated successfully!\n");

        } catch (Exception e) {
            System.out.println("\nAn error occurred while updating the room rate: " + e.getMessage() + "\n");
        }
    }

    private void doDeleteRoomRate() {

        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Delete room rate ***\n");

            displayRoomNames();

            System.out.print("\nEnter room rate name: ");
            String rateNameString = scanner.nextLine().trim();

            RoomRate existingRoomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomName(rateNameString);

            System.out.println("\nRoom rate found. Details for room rate name: " + existingRoomRate.getRateName() + "");
            System.out.println("Room Type: " + existingRoomRate.getRoomType().getTypeName());
            System.out.println("Room Rate Type: " + formatEnumString(existingRoomRate.getRateType().toString()));

            // check if rate is PROMOTION or PEAK
            if (existingRoomRate.getRateType() == RoomRateTypeEnum.PROMOTION) {
                System.out.println("Promotion Start Date: " + existingRoomRate.getPromotionStartDate());
                System.out.println("Promotion End Date: " + existingRoomRate.getPromotionEndDate());
            } else if (existingRoomRate.getRateType() == RoomRateTypeEnum.PEAK) {
                System.out.println("Peak Start Date: " + existingRoomRate.getPeakStartDate());
                System.out.println("Peak End Date: " + existingRoomRate.getPeakEndDate());
            }

            System.out.println("Rate Per Night: $" + existingRoomRate.getRatePerNight());

            System.out.print("\nAre you sure you want to delete this room rate? (Y/N): ");
            String response = scanner.nextLine().trim();

            if (response.toLowerCase().equals("y")) {
                roomRateSessionBeanRemote.deleteRoomRate(existingRoomRate);
                System.out.println("Room type has successfully been removed.\n");
            } else if (response.toLowerCase().equals("n")) {
                System.out.println("\n");
            } else {
                System.out.println("Invalid option. Please try again. \n");
            }

        } catch (Exception ex) {
            System.out.println("Error deleting room: " + ex.getMessage() + "\n");
        }

    }

    private void doViewAllRoomRates() {
        try {
            System.out.println("*** HoRS Management Client :: View All Room Rates ***\n");

            // Header with a separator line
            System.out.printf("%-5s %-30s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s\n",
                    "No.", "Rate Name", "Rate Type", "Rate Per Night", "Room Rate Status",
                    "Room Type", "Promotion Start Date", "Promotion End Date", "Peak Start Date", "Peak End Date");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            int index = 1;
            List<RoomRate> roomRates = roomRateSessionBeanRemote.retrieveAllRoomRates();

            if (roomRates.isEmpty()) {
                System.out.println("No room rates found.");
            } else {
                for (RoomRate roomRate : roomRates) {
                    System.out.printf("%-5d %-30s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s\n",
                            index++,
                            roomRate.getRateName(),
                            formatEnumString(roomRate.getRateType().toString()),
                            formatCurrency(roomRate.getRatePerNight()),
                            formatEnumString(roomRate.getRoomRateStatus().toString()),
                            roomRate.getRoomType().getTypeName(),
                            formatDate(roomRate.getPromotionStartDate()),
                            formatDate(roomRate.getPromotionEndDate()),
                            formatDate(roomRate.getPeakStartDate()),
                            formatDate(roomRate.getPeakEndDate()));
                }
            }

            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        } catch (Exception ex) {
            System.out.println("Error viewing all room rates: " + ex.getMessage());
            ex.printStackTrace(); // For debugging purposes
        }
    }

    /**
     * ALL GUEST RELATION OFFICER METHODS *
     */
    private void showGuestRelationOfficerMenu() {
        try {

            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                //This is to check if user initiated the logout command
                if (currentEmployee == null) {
                    break;
                }

                System.out.println("*** HoRS Management Client ::  Welcome, " + currentEmployee.getUsername() + ". What can i do for you today?***\n");
                System.out.println("1: Walk-in search room");
                System.out.println("2: Walk-in reserve room");
                System.out.println("3: Check-in guest");
                System.out.println("4: Check-out guest");
                System.out.println("5: Logout\n");
                response = 0;

                while (response < 1 || response > 5) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doWalkInSearchRoom();
                    } else if (response == 2) {
                        doWalkInReserveRoom();
                    } else if (response == 3) {
                        doCheckInGuest();
                    } else if (response == 4) {
                        doCheckOutGuest();
                    } else if (response == 5) { //Since before this is the first screen, can just do a break
                        System.out.println("Logging out...\n");
                        currentEmployee = null;
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again. \n");
                    }
                }

                if (response == 5) {
                    break;
                }

            }

        } catch (Exception ex) {
            System.out.println("Error showing guest relation officer menu: " + ex.getMessage() + "\n");
        }

    }

    private void doWalkInSearchRoom() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Walk-In Search Room ***\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Prompt for check-in and check-out dates
            Date checkInDate = promptForDate(scanner, dateFormat, "Enter check-in date (yyyy-MM-dd): ");
            Date checkOutDate;
            while (true) {
                checkOutDate = promptForDate(scanner, dateFormat, "Enter check-out date (yyyy-MM-dd): ");
                if (!checkOutDate.equals(checkInDate)) {
                    break;
                }
                System.out.println("Check-out date cannot be the same as the check-in date. Please enter a different check-out date.");
            }

            // Prompt for the number of rooms requested
            System.out.print("Enter number of rooms you would like to book: ");
            int noOfRooms = Integer.parseInt(scanner.nextLine().trim());

            // Retrieve available rooms
            List<Room> availableRooms = roomSessionBeanRemote.retrieveAllAvailableRooms();
            if (availableRooms == null || availableRooms.isEmpty()) {
                System.out.println("\nNo available rooms found for the selected dates.");
                return;
            }

            // Call reserveWalkInRoom to display rooms and proceed with reservation logic if applicable
            reserveWalkInRoom(checkInDate, checkOutDate, noOfRooms, availableRooms);

        } catch (Exception ex) {
            System.out.println("Error searching for hotel room: " + ex.getMessage());
        }
    }

    private void doWalkInReserveRoom() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Walk-In Reserve Room ***\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            //prompt for check-in and check-out dates
            Date checkInDate = promptForDate(scanner, dateFormat, "Enter check-in date (yyyy-MM-dd): ");
            Date checkOutDate;
            while (true) {
                checkOutDate = promptForDate(scanner, dateFormat, "Enter check-out date (yyyy-MM-dd): ");
                if (!checkOutDate.equals(checkInDate)) {
                    break;
                }
                System.out.println("Check-out date cannot be the same as the check-in date. Please enter a different check-out date.");
            }

            //prompt for the number of rooms requested
            System.out.print("Enter number of rooms you would like to book: ");
            int noOfRooms = Integer.parseInt(scanner.nextLine().trim());

            //get available rooms
            List<Room> availableRooms = roomSessionBeanRemote.retrieveAllAvailableRooms();
            if (availableRooms == null || availableRooms.isEmpty()) {
                System.out.println("\nNo available rooms found for the selected dates.");
                return;
            }

            //call reserveWalkInRoom to display rooms and handle reservation if applicable
            reserveWalkInRoom(checkInDate, checkOutDate, noOfRooms, availableRooms);

        } catch (Exception ex) {
            System.out.println("Error in reserving hotel room: " + ex.getMessage());
        }
    }

    private void reserveWalkInRoom(Date checkInDate, Date checkOutDate, int noOfRooms, List<Room> availableRooms) {
        try {
            List<RoomType> roomTypes = new ArrayList<>();
            List<BigDecimal> totalPerRoomAmounts = new ArrayList<>();
            List<List<Room>> roomsByType = new ArrayList<>();

            // Group rooms by RoomType
            for (Room room : availableRooms) {
                RoomType roomType = room.getRoomType();
                int index = roomTypes.indexOf(roomType);

                if (index == -1) { // Room type not yet in the list
                    roomTypes.add(roomType);
                    totalPerRoomAmounts.add(BigDecimal.ZERO);
                    roomsByType.add(new ArrayList<>()); // Create a new list for this room type
                    index = roomTypes.size() - 1; // Update index to the newly added room type
                }
                roomsByType.get(index).add(room); // Add room to the corresponding list
            }

            // Display available room types and calculate total prices
            System.out.println("Available rooms for the selected dates:\n");
            List<Integer> selectableIndices = new ArrayList<>();

            for (int i = 0; i < roomTypes.size(); i++) {
                RoomType roomType = roomTypes.get(i);
                List<Room> roomsOfThisType = roomsByType.get(i);

                if (roomsOfThisType.size() >= noOfRooms) {
                    // Add this index to selectable indices as it meets the room requirement
                    selectableIndices.add(i);

                    // Calculate the per-room price for this room type
                    BigDecimal totalPerRoomAmount = BigDecimal.ZERO;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(checkInDate);

                    while (calendar.getTime().before(checkOutDate)) {
                        Date currentDate = calendar.getTime();
                        BigDecimal ratePerNight = roomTypeSessionBeanRemote.getLowestTierDailyRate(currentDate, ReservationTypeEnum.WALKIN, roomsOfThisType);

                        if (ratePerNight != null) {
                            totalPerRoomAmount = totalPerRoomAmount.add(ratePerNight);
                        }
                        calendar.add(Calendar.DATE, 1);
                    }

                    totalPerRoomAmounts.set(i, totalPerRoomAmount);

                    // Display each room type with a selection number
                    System.out.println((selectableIndices.size()) + ": " + roomType.getTypeName()
                            + " - $" + totalPerRoomAmount
                            + " per room, Total Reservation Amount: $"
                            + totalPerRoomAmount.multiply(BigDecimal.valueOf(noOfRooms))
                            + " for " + noOfRooms + ((noOfRooms > 1) ? " rooms" : " room"));

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

            System.out.println("\nYou selected: " + selectedRoomType.getTypeName()
                    + " - $" + selectedTotalPerRoomAmount
                    + " per room, Total Reservation Amount: $"
                    + selectedTotalPerRoomAmount.multiply(BigDecimal.valueOf(noOfRooms))
                    + " for " + noOfRooms + " rooms");

            // Proceed with reservation confirmation
            System.out.println("\nWould you like to proceed with registration and reservation?");
            System.out.println("1: Reserve Room(s)");
            System.out.println("2: Go back");

            System.out.print("\nEnter your choice > ");
            int response = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (response == 1) {
                Guest verifiedGuest = doGuestVerification();
                if (verifiedGuest == null) {
                    System.out.println("Unable to verify guest.");
                    return;
                }

                // Re-fetch the latest available rooms after confirmation
                List<Room> latestAvailableRooms = roomSessionBeanRemote.retrieveAllAvailableRooms();

                // Group the latest available rooms by RoomType
                Map<RoomType, List<Room>> latestRoomsByType = new HashMap<>();
                for (Room room : latestAvailableRooms) {
                    latestRoomsByType.computeIfAbsent(room.getRoomType(), k -> new ArrayList<>()).add(room);
                }

                // Create a single reservation for the total number of rooms
                int index = roomTypes.indexOf(selectedRoomType);

                // Calculate the total reservation amount based on the requested room type and total number of rooms
                BigDecimal totalReservationAmount = totalPerRoomAmounts.get(index)
                        .multiply(BigDecimal.valueOf(noOfRooms));

                int roomsToBook = noOfRooms;
                List<Room> roomsToReserve = new ArrayList<>();

                // Attempt to reserve rooms in the requested room type only
                List<Room> latestRoomsOfRequestedType = latestRoomsByType.getOrDefault(selectedRoomType, new ArrayList<>());
                int availableRoomsOfRequestedType = latestRoomsOfRequestedType.size();

                int roomsToBookFromRequestedType = Math.min(roomsToBook, availableRoomsOfRequestedType);
                roomsToBook -= roomsToBookFromRequestedType;

                // Mark rooms as RESERVED
                for (int j = 0; j < roomsToBookFromRequestedType; j++) {
                    Room room = latestRoomsOfRequestedType.get(j);
                    room.setRoomStatus(RoomStatusEnum.RESERVED);
                    roomSessionBeanRemote.updateRoom(room);
                    roomsToReserve.add(room);
                }

                // Create the reservation
                Reservation reservation = new Reservation(
                        checkInDate,
                        checkOutDate,
                        noOfRooms,
                        ReservationTypeEnum.WALKIN,
                        totalReservationAmount,
                        ReservationStatusEnum.CONFIRMED,
                        verifiedGuest,
                        selectedRoomType,
                        null
                );

                reservationSessionBeanRemote.createNewReservation(reservation);

                // Immediate allocation if applicable
                if (stripTime(reservation.getCheckInDate()).equals(stripTime(new Date()))) {
                    Calendar currentTime = Calendar.getInstance();
                    int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                    if (currentHour >= 2) {
                        roomSessionBeanRemote.allocateRooms();
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
        }
    }

    private void doCheckInGuest() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Check-in guest ***\n");

            Guest guest = doCheckInAndOutVerification();
            if (guest == null) {
                System.out.println("Unable to verify guest");
                return;
            }

            // Retrieve all reservations for the guest and filter those with today's check-in date
            List<Reservation> reservations = reservationSessionBeanRemote.retrieveAllReservationsByGuest(guest);
            List<Reservation> todayReservations = new ArrayList<>();
            Date today = stripTime(new Date());

            for (Reservation reservation : reservations) {
                if (stripTime(reservation.getCheckInDate()).equals(today)) {
                    todayReservations.add(reservation);
                }
            }

            // If there are any reservations for today, ask if they want to check in to all
            if (!todayReservations.isEmpty()) {
                System.out.println("\nYou have reservations scheduled for today:");
                for (Reservation reservation : todayReservations) {
                    System.out.println("- Reservation ID: " + reservation.getReservationId() + ", Room Type: " + reservation.getRoomType().getTypeName());
                }

                System.out.print("\nDo you want to check in to all available rooms for these reservations? (Y/N): ");
                String response = scanner.nextLine().trim().toLowerCase();

                if (response.equals("y")) {
                    // Process each reservation for check-in
                    for (Reservation reservation : todayReservations) {
                        List<RoomReservation> roomReservations = roomReservationSessionBeanRemote.retrieveRoomReservationsByReservation(reservation);
                        List<RoomReservation> allocatedRooms = new ArrayList<>();

                        for (RoomReservation roomReservation : roomReservations) {
                            if (roomReservation.getRoom() != null && roomReservation.getRoom().getRoomStatus() == RoomStatusEnum.ALLOCATED) {
                                allocatedRooms.add(roomReservation);
                            }
                        }

                        if (!allocatedRooms.isEmpty()) {
                            System.out.println("Checking in to reservation ID: " + reservation.getReservationId());
                            System.out.println("The following rooms are ready for check-in:");

                            for (RoomReservation allocatedRoom : allocatedRooms) {
                                System.out.println("Room Number: " + allocatedRoom.getRoom().getRoomNum());
                                // Update the room's status to OCCUPIED
                                allocatedRoom.getRoom().setRoomStatus(RoomStatusEnum.OCCUPIED);
                                roomSessionBeanRemote.updateRoom(allocatedRoom.getRoom());
                            }

                            // Update reservation status to CHECKED_IN
                            reservation.setReservationStatusEnum(ReservationStatusEnum.CHECKED_IN);
                            reservationSessionBeanRemote.updateReservation(reservation);
                            System.out.println("Successfully checked in to reservation ID: " + reservation.getReservationId() + "\n");
                        } else {
                            System.out.println("Reservation ID: " + reservation.getReservationId() + " - Your rooms are not ready yet. Unable to check you in.");
                        }
                    }
                } else {
                    System.out.println("Check-in canceled.\n");
                }
            } else {
                System.out.println("No reservations scheduled for today.");
            }

        } catch (Exception ex) {
            System.out.println("Error checking in: " + ex.getMessage());
        }
    }

    private void doCheckOutGuest() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Check-out guest ***\n");

            // Verify the guest
            Guest guest = doCheckInAndOutVerification();
            if (guest == null) {
                System.out.println("Unable to verify guest");
                return;
            }

            // Retrieve all reservations for the guest and filter those with today's check-out date
            List<Reservation> reservations = reservationSessionBeanRemote.retrieveAllReservationsByGuest(guest);
            List<Reservation> todayCheckOutReservations = new ArrayList<>();
            Date today = stripTime(new Date());

            for (Reservation reservation : reservations) {
                if (stripTime(reservation.getCheckOutDate()).equals(today)) {
                    todayCheckOutReservations.add(reservation);
                }
            }

            // If there are any reservations for check-out today, ask if they want to check out of all
            if (!todayCheckOutReservations.isEmpty()) {
                System.out.println("\nYou have reservations scheduled for check-out today:");
                for (Reservation reservation : todayCheckOutReservations) {
                    System.out.println("- Reservation ID: " + reservation.getReservationId() + ", Room Type: " + reservation.getRoomType());
                }

                System.out.print("\nDo you want to check out of all these reservations? (Y/N): ");
                String response = scanner.nextLine().trim().toLowerCase();

                if (response.equals("y")) {
                    // Process each reservation for check-out
                    for (Reservation reservation : todayCheckOutReservations) {
                        List<RoomReservation> roomReservations = roomReservationSessionBeanRemote.retrieveRoomReservationsByReservation(reservation);
                        List<RoomReservation> occupiedRooms = new ArrayList<>();

                        for (RoomReservation roomReservation : roomReservations) {
                            if (roomReservation.getRoom() != null && roomReservation.getRoom().getRoomStatus() == RoomStatusEnum.OCCUPIED) {
                                occupiedRooms.add(roomReservation);
                            }
                        }

                        if (!occupiedRooms.isEmpty()) {
                            System.out.println("Checking out of reservation ID: " + reservation.getReservationId());
                            System.out.println("The following rooms are being checked out:");

                            for (RoomReservation occupiedRoom : occupiedRooms) {
                                System.out.println("Room Number: " + occupiedRoom.getRoom().getRoomNum());
                                // Update the room's status to AVAILABLE
                                occupiedRoom.getRoom().setRoomStatus(RoomStatusEnum.AVAILABLE);
                                roomSessionBeanRemote.updateRoom(occupiedRoom.getRoom());
                            }

                            // Update reservation status to CHECKED_OUT
                            reservation.setReservationStatusEnum(ReservationStatusEnum.CHECKED_OUT);
                            reservationSessionBeanRemote.updateReservation(reservation);
                            System.out.println("Successfully checked out of reservation ID: " + reservation.getReservationId() + "\n");
                        } else {
                            System.out.println("Reservation ID: " + reservation.getReservationId() + " - No occupied rooms found for this reservation.");
                        }
                    }
                } else {
                    System.out.println("Check-out canceled.\n");
                }
            } else {
                System.out.println("No reservations scheduled for check-out today.");
            }

        } catch (Exception ex) {
            System.out.println("Error checking out: " + ex.getMessage());
        }
    }

    /**
     * HELPER METHOD *
     */
    private String formatEnumString(String enumString) {
        String[] words = enumString.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return formatted.toString().trim();
    }

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

    private Date stripTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Guest doGuestVerification() {

        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Guest verification ***\n");

            System.out.print("Are you a registered guest? (Y/N): ");
            String response = scanner.nextLine().trim();

            if (response.toLowerCase().equals("y")) {
                return doRegisteredGuestReservation();

            } else if (response.toLowerCase().equals("n")) {
                return doNonRegisteredGuestReservation();
            } else {
                System.out.println("Invalid option. Please try again. \n");
            }

        } catch (Exception ex) {
            System.out.println("Error verifying guest: " + ex.getMessage() + "\n");
        }

        return null;
    }

    private Guest doRegisteredGuestReservation() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                System.out.println("*** HoRS Management Client ::  Registered guest verification***\n");
                System.out.println("1: Verify by email");
                System.out.println("2: Verify by phone number");
                System.out.println("3: Verify by passport number");
                System.out.println("4: Go back\n");
                response = 0;

                while (response < 1 || response > 4) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        System.out.print("Enter email address: ");
                        scanner.nextLine();  // Consume the leftover newline
                        String email = scanner.nextLine().trim();
                        return verifyGuestByEmail(email);
                    } else if (response == 2) {
                        System.out.print("Enter phone number: ");
                        scanner.nextLine();  // Consume the leftover newline
                        String phoneNumber = scanner.nextLine().trim();
                        return verifyGuestByPhoneNumber(phoneNumber);

                    } else if (response == 3) {
                        System.out.print("Enter passport number: ");
                        scanner.nextLine();  // Consume the leftover newline
                        String passportNumber = scanner.nextLine().trim();
                        return verifyGuestByPassportNumber(passportNumber);

                    } else if (response == 4) {
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again.\n");
                    }

                }

                if (response == 4) {
                    break;
                }

            }

        } catch (Exception ex) {
            System.out.println("Error verifying registered guest: " + ex.getMessage() + "\n");
        }

        return null;
    }

    private Guest doNonRegisteredGuestReservation() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Non-registered guest verification ***\n");

            System.out.print("Enter guest first name: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Enter guest last name: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Enter guest email address: ");
            String emailAddress = scanner.nextLine().trim();

            System.out.print("Enter guest phone number: ");
            String phoneNumber = scanner.nextLine().trim();

            System.out.print("Enter guest passport number: ");
            String passportNumber = scanner.nextLine().trim();

            if (firstName.length() > 0 && lastName.length() > 0 && emailAddress.length() > 0 && phoneNumber.length() > 0 && passportNumber.length() > 0) {
                Guest guest = customerSessionBeanRemote.createNewCustomer(new Customer(firstName, lastName, emailAddress, passportNumber, passportNumber));
                System.out.println("\nGuest verified successfully.\n");
                return guest;
            } else {
                throw new InvalidAccountCredentialsException("Invalid guest credentials");
            }

        } catch (Exception ex) {
            System.out.println("Error verifying non-registered guest: " + ex.getMessage() + "\n");
        }

        return null;
    }

    private Guest doCheckInAndOutVerification() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;

            while (true) {

                System.out.println("*** HoRS Management Client ::  Guest verification***\n");
                System.out.println("1: Verify by email");
                System.out.println("2: Verify by phone number");
                System.out.println("3: Verify by passport number");
                System.out.println("4: Go back\n");
                response = 0;

                while (response < 1 || response > 4) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        System.out.print("Enter email address: ");
                        scanner.nextLine();  // Consume the leftover newline
                        String email = scanner.nextLine().trim();
                        return verifyGuestByEmail(email);

                    } else if (response == 2) {
                        System.out.print("Enter phone number: ");
                        scanner.nextLine();  // Consume the leftover newline
                        String phoneNumber = scanner.nextLine().trim();
                        return verifyGuestByPhoneNumber(phoneNumber);

                    } else if (response == 3) {
                        System.out.print("Enter passport number: ");
                        scanner.nextLine();  // Consume the leftover newline
                        String passportNumber = scanner.nextLine().trim();
                        return verifyGuestByPassportNumber(passportNumber);

                    } else if (response == 4) {
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again.\n");
                    }
                }

                if (response == 4) {
                    break;
                }

            }

        } catch (Exception ex) {
            System.out.println("Error verifying guest: " + ex.getMessage() + "\n");
        }

        return null;
    }

    private Guest verifyGuestByEmail(String email) {
        try {
            Guest guest = guestSessionBeanRemote.retrieveGuestByEmail(email);
            if (guest != null) {
                System.out.println("Guest verified successfully");
                return guest;
            } else {
                System.out.println("No matching customer found for the provided email.");
            }
        } catch (Exception ex) {
            System.out.println("Error verifying guest by email: " + ex.getMessage());
        }
        return null;
    }

    private Guest verifyGuestByPhoneNumber(String phoneNumber) {
        try {
            Guest guest = guestSessionBeanRemote.retrieveGuestByPhoneNumber(phoneNumber);
            if (guest != null) {
                System.out.println("Guest verified successfully");
                return guest;
            } else {
                System.out.println("No matching customer found for the provided phone number.");
            }
        } catch (Exception ex) {
            System.out.println("Error verifying guest by phone number: " + ex.getMessage());
        }
        return null;
    }

    private Guest verifyGuestByPassportNumber(String passportNumber) {
        try {
            Guest guest = guestSessionBeanRemote.retrieveGuestByPassportNumber(passportNumber);
            if (guest != null) {
                System.out.println("Guest verified successfully");
                return guest;
            } else {
                System.out.println("No matching customer found for the provided passport number.");
            }
        } catch (Exception ex) {
            System.out.println("Error verifying guest by passport number: " + ex.getMessage());
        }
        return null;
    }

    private void displayRoomNames() {
        try {
            List<RoomRate> roomRates = roomRateSessionBeanRemote.retrieveAllRoomRates();

            System.out.println("Available Room Rates:");
            for (int i = 0; i < roomRates.size(); i++) {
                System.out.println(roomRates.get(i).getRateName());
            }

        } catch (Exception ex) {
            System.out.println("Error retrieving room rate list: " + ex.getMessage());
        }
    }

    private void displayRoomTypes() {
        try {
            List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();

            System.out.println("Room Types:");
            for (int i = 0; i < roomTypes.size(); i++) {
                System.out.println(roomTypes.get(i).getTypeName());
            }

        } catch (Exception ex) {
            System.out.println("Error retrieving room types: " + ex.getMessage());
        }
    }

    private void displayActiveRoomTypes() {
        try {
            List<RoomType> acriveRoomTypes = roomTypeSessionBeanRemote.retrieveAllActiveRoomTypes();

            System.out.println("Available Room Types:");
            for (int i = 0; i < acriveRoomTypes.size(); i++) {
                System.out.println(acriveRoomTypes.get(i).getTypeName());
            }

        } catch (Exception ex) {
            System.out.println("Error retrieving room types: " + ex.getMessage());
        }
    }

    private void displayRoomNumbers() {
        try {
            List<Room> rooms = roomSessionBeanRemote.retrieveAllRooms();

            System.out.println("Available Room Numbers:");
            int columns = 5;
            for (int i = 0; i < rooms.size(); i++) {
                // Print each room number with some spacing
                System.out.printf("%-6s", rooms.get(i).getRoomNum());

                // Move to the next line after every 'columns' entries
                if ((i + 1) % columns == 0) {
                    System.out.println();
                }
            }

            if (rooms.size() % columns != 0) {
                System.out.println();
            }

        } catch (Exception ex) {
            System.out.println("Error retrieving room numbers: " + ex.getMessage());
        }
    }

    // Helper method to format currency values
    private String formatCurrency(BigDecimal amount) {
        return amount != null ? "$" + amount.toString() : "N/A";
    }

    // Helper method to format dates to avoid null display
    private String formatDate(Date date) {
        return date != null ? new SimpleDateFormat("dd-MM-yyyy").format(date) : "N/A";
    }
}
