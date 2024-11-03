/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ExceptionReportSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomReservationSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
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
import util.enumeration.PartnerAccessRightEnum;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

            System.out.println("*** HoRS Management Client :: View all employees ***\n");
            System.out.printf("%-5s %-20s %-20s\n", "No.", "Username", "Role");
            int index = 1;

            //Do not need to check if list is empty because it will never happen 
            List<Employee> employees = employeeSessionBeanRemote.retrieveAllEmployees();
            for (Employee employee : employees) {
                System.out.printf("%-5d %-20s %-20s\n",
                        index++,
                        employee.getUsername(),
                        formatEnumString(employee.getRole().toString()));
            }
            System.out.print("\n");

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

            System.out.println("Select partner role: ");
            PartnerAccessRightEnum[] roles = PartnerAccessRightEnum.values();
            for (int i = 0; i < roles.length; i++) {
                System.out.println((i + 1) + ") " + formatEnumString(roles[i].toString()));
            }

            int response = 0;
            while (true) {
                System.out.print("Enter your choice > ");
                response = scanner.nextInt();

                if (response >= 1 && response <= roles.length) {
                    PartnerAccessRightEnum role = roles[response - 1];
                    scanner.nextLine();

                    if (username.length() > 0 && password.length() > 0) {
                        Partner newPartner = new Partner(username, password, role);
                        partnerSessionBeanRemote.createNewPartner(newPartner);
                        System.out.println("Partner has successfully been created!\n");
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

    private void doViewAllPartners() {
        try {

            System.out.println("*** HoRS Management Client :: View all partners ***\n");

            List<Partner> partners = partnerSessionBeanRemote.retrieveAllPartners();

            if (partners.isEmpty()) {
                System.out.println("No partners found.");
            } else {
                System.out.printf("%-5s %-20s %-20s\n", "No.", "Username", "Role");
                int index = 1;

                for (Partner partner : partners) {
                    System.out.printf("%-5d %-20s %-20s\n",
                            index++,
                            partner.getUsername(),
                            formatEnumString(partner.getRole().toString()));
                }
            }
            System.out.print("\n");

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
                System.out.println("4: Logout\n");
                response = 0;

                while (response < 1 || response > 4) {
                    System.out.print("Enter your choice > ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doManageRoomTypes();
                    } else if (response == 2) {
                        doManageRooms();
                    } else if (response == 3) {
                        doViewRoomAllocationExceptionReport();
                    } else if (response == 4) { //Since before this is the first screen, can just do a break
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

            System.out.println("New room type '" + name + "' has successfully been created.");

        } catch (Exception ex) {
            System.out.println("Error creating new room type: " + ex.getMessage() + "\n");
        }
    }

    private void doViewRoomTypeDetails() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: View room type details ***\n");

            System.out.print("Enter room type name: ");
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

            System.out.print("Enter room type name: ");
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

            System.out.print("Enter room type name: ");
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
            System.out.println("*** HoRS Management Client :: View all room types ***\n");
            System.out.printf("%-5s %-20s %-20s %-20s %-20s %-20s\n",
                    "No.", "Name", "Capacity", "Vacancy", "Tier", "Status");
            int index = 1;

            List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();
            if (roomTypes.isEmpty()) {
                System.out.println("No room types found.");
            } else {
                for (RoomType roomType : roomTypes) {
                    System.out.printf("%-5d %-20s %-20s %-20s %-20s %-20s\n",
                            index++,
                            roomType.getTypeName(),
                            roomType.getCapacity(),
                            0, //Rmb to change it to the correct method for get number of available rooms left
                            roomType.getTierNumber(),
                            formatEnumString(roomType.getRoomTypeStatus().toString()));
                }
            }

            System.out.print("\n");

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

                System.out.println("*** HoRS Management Client :: Manage room types ***\n");
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

                if (response == 6 || response == 7) {
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

            displayRoomTypes();

            System.out.print("\nEnter room type: ");
            String roomTypeString = scanner.nextLine().trim();

            RoomType existingRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByName(roomTypeString);

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

            System.out.println("New room for '" + roomTypeString + "' has successfully been created.");
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
                System.out.println("\nA room with room number " + roomNum + " already exists. Room creation aborted.");
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
            System.out.println("*** HoRS Management Client :: View all rooms ***\n");
            System.out.printf("%-5s %-20s %-20s %-20s %-20s %-20s\n",
                    "No.", "Room number", "Floor Number", "Sequence Number", "Room type", "Status");
            int index = 1;

            List<Room> rooms = roomSessionBeanRemote.retrieveAllRooms();

            if (rooms.isEmpty()) {
                System.out.println("No room types found.");
            } else {
                for (Room room : rooms) {
                    System.out.printf("%-5d %-20s %-20s %-20s %-20s %-20s\n",
                            index++,
                            room.getRoomNum(),
                            room.getFloorNum(),
                            room.getSequenceNum(),
                            room.getRoomType().getTypeName(),
                            formatEnumString(room.getRoomStatus().toString()));
                }
            }

            System.out.print("\n");

        } catch (Exception ex) {
            System.out.println("Error viewing all rooms: " + ex.getMessage() + "\n");
        }

    }

    private void doViewRoomAllocationExceptionReport() {
        try {

            System.out.println("*** HoRS Management Client :: View Room Allocation Exception Report ***\n");
            System.out.printf("%-5s %-20s %-20s %-20s\n", "No.", "Type", "Reservation No.", "Remarks");
            int index = 1;

            //Do not need to check if list is empty because it will never happen 
            List<ExceptionReport> exceptionReports = exceptionReportSessionBeanRemote.retrieveAllExceptionReports();
            for (ExceptionReport exceptionReport : exceptionReports) {
                
                System.out.printf("%-5d %-20s %-20s %-20s\n",
                        index++,
                        formatEnumString(exceptionReport.getExceptionTypeReport().toString()),
                        exceptionReport.getRoomReservation().getReservation().getReservationId(),
                        exceptionReport.getExceptionTypeReport() == ExceptionTypeReportEnum.TYPE_1 ? 
                                "Upgraded from " + exceptionReport.getRoomReservation().getReservation().getRoomType().getTypeName() + " to " + exceptionReport.getRoomReservation().getRoom().getRoomType().getTypeName() :
                                "No upgrade to next higher tier"
                );
            }
            System.out.print("\n");

        } catch (Exception ex) {
            System.out.println("Error viewing room allocation exception reports: " + ex.getMessage() + "\n");
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

                if (response == 3) {
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

            System.out.println("New room rate '" + name + "' has successfully been created.");

        } catch (Exception ex) {
            System.out.println("Error creating new room rate: " + ex.getMessage() + "\n");
        }

    }

    private void doViewRoomRateDetails() {
        try {

            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: View Room Rate Details ***\n");

            displayRoomNames();

            System.out.print("Enter room rate name: ");
            String rateNameString = scanner.nextLine().trim();

            RoomRate existingRoomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomName(rateNameString);

            System.out.println("\nRoom type found. Details for room rate name: " + existingRoomRate.getRateName() + "");
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

            System.out.print("\n Enter room rate name: ");
            String rateNameString = scanner.nextLine().trim();

            RoomRate existingRoomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomName(rateNameString);

            System.out.println("\nRoom type found. Details for room rate name: " + existingRoomRate.getRateName() + "");
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

            // doViewRoomRateDetails();
            RoomRate updatedRoomRate = new RoomRate();
            updatedRoomRate.setRoomRateId(existingRoomRate.getRoomRateId());

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

                    if (newRateType == RoomRateTypeEnum.PROMOTION || newRateType == RoomRateTypeEnum.PEAK) {
                        // Get the current date
                        Date currentDate = new Date();
                        Date startDate = null;
                        Date endDate = null;
                        boolean validDates = false;

                        while (!validDates) {
                            try {
                                System.out.print("Enter start date (yyyy-MM-dd): ");
                                String startDateInput = scanner.nextLine().trim();
                                System.out.print("Enter end date (yyyy-MM-dd): ");
                                String endDateInput = scanner.nextLine().trim();

                                startDate = dateFormat.parse(startDateInput);
                                endDate = dateFormat.parse(endDateInput);

                                if (startDate.before(currentDate)) {
                                    System.out.println("Start date must be after today's date.");
                                } else if (endDate.before(startDate)) {
                                    System.out.println("End date must be after the start date.");
                                } else {
                                    validDates = true; // Exit the loop if both dates are valid
                                }
                            } catch (ParseException ex) {
                                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                            }
                        }

                        // Set dates after validation
                        if (newRateType == RoomRateTypeEnum.PROMOTION) {
                            updatedRoomRate.setPromotionStartDate(startDate);
                            updatedRoomRate.setPromotionEndDate(endDate);
                        } else if (newRateType == RoomRateTypeEnum.PEAK) {
                            updatedRoomRate.setPeakStartDate(startDate);
                            updatedRoomRate.setPeakEndDate(endDate);
                        } else {
                            updatedRoomRate.setPromotionStartDate(null);
                            updatedRoomRate.setPromotionEndDate(null);
                            updatedRoomRate.setPeakStartDate(null);
                            updatedRoomRate.setPeakEndDate(null);
                        }
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
                    System.out.print("Invalid input for rate per night. Keeping current value");
                }
            }

            roomRateSessionBeanRemote.updateRoomRate(updatedRoomRate);
            System.out.println("Room rate updated successfully!");

        } catch (Exception e) {
            System.out.println("An error occurred while updating the room rate: " + e.getMessage());
        }
    }

    private void doDeleteRoomRate() {

        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Delete room rate ***\n");

            displayRoomNames();

            System.out.print("Enter room rate name: ");
            String rateNameString = scanner.nextLine().trim();

            RoomRate existingRoomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomName(rateNameString);

            System.out.println("\nRoom type found. Details for room rate name: " + existingRoomRate.getRateName() + "");
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
            System.out.println("*** HoRS Management Client :: View all room rates***\n");
            System.out.printf("%-5s %-20s %-20s %-20s %-20s %-20s %-40s %-40s %-40s %-40s\n",
                    "No.", "Rate Name", "Rate Type", "Rate Per Night", "Room Rate Status", "Room Type", "Promotion Start Date", "Promotion End Date", "Peak Start Date", "Peak End Date");
            int index = 1;

            List<RoomRate> roomRates = roomRateSessionBeanRemote.retrieveAllRoomRates();

            if (roomRates.isEmpty()) {
                System.out.println("No room rates found.");
            } else {
                for (RoomRate roomRate : roomRates) {
                    System.out.printf("%-5d %-20s %-20s %-20s %-20s %-20s %-40s %-40s %-40s %-40s\n",
                            index++,
                            roomRate.getRateName(),
                            formatEnumString(roomRate.getRateType().toString()),
                            roomRate.getRatePerNight(),
                            formatEnumString(roomRate.getRoomRateStatus().toString()),
                            roomRate.getRoomType().getTypeName(),
                            roomRate.getPromotionStartDate(),
                            roomRate.getPromotionEndDate(),
                            roomRate.getPeakStartDate(),
                            roomRate.getPeakEndDate());
                }
            }

            System.out.print("\n");

        } catch (Exception ex) {
            System.out.println("Error viewing all rooms: " + ex.getMessage() + "\n");
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
                            reserveWalkInRoom(checkInDate, checkOutDate, noOfRooms, availableRooms);
                            System.out.println("Returning to the main menu...\n");
                            break;
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

    private void doWalkInReserveRoom() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Walk-In Reserve Room ***\n");

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
                reserveWalkInRoom(checkInDate, checkOutDate, noOfRooms, availableRooms);
            } else {
                System.out.println("Not enough rooms are available. Available rooms: " + availableRoomSize);
            }

        } catch (Exception ex) {
            System.out.println("Error in reserving hotel room: " + ex.getMessage());
        }
    }

    private void reserveWalkInRoom(Date checkInDate, Date checkOutDate, int noOfRooms, List<Room> availableRooms) {
        try {
            if (reservationSessionBeanRemote == null) {
                System.out.println("Error: reservationSessionBeanRemote is not initialized.");
                return;
            }
            if (roomRateSessionBeanRemote == null) {
                System.out.println("Error: roomRateSessionBeanRemote is not initialized.");
                return;
            }

            if (availableRooms == null || availableRooms.isEmpty()) {
                System.out.println("Error: No available rooms provided.");
                return;
            }

            Scanner scanner = new Scanner(System.in);

            // Verify guest information
            Guest verifiedGuest = doGuestVerification();
//            if (verifiedGuest == null) {
//                System.out.println("Guest verification failed. Reservation process terminated.");
//            }

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
                    BigDecimal ratePerNight = roomTypeSessionBeanRemote.getLowestTierDailyRate(currentDate, ReservationTypeEnum.WALKIN, availableRooms);

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
                        verifiedGuest,
                        roomType,
                        null
                );

                reservationSessionBeanRemote.createNewReservation(reservation);
                // System.out.println("Reservation created successfully for Room Type: " + roomType.getTypeName());

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

    private void doCheckInGuest() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Check-in guest ***\n");

            System.out.print("Enter reservation ID: ");
            Long reservationId = scanner.nextLong();
            scanner.nextLine();
                    
            Reservation reservation = reservationSessionBeanRemote.getReservationByReservationId(reservationId);

            Date checkInDate = stripTime(reservation.getCheckInDate());
            Date today = stripTime(new Date());
            
            if (checkInDate.equals(today)) {
                // Check if the current time is before 2 pm
                Calendar currentTime = Calendar.getInstance();
                int currentHour = currentTime.get(Calendar.HOUR_OF_DAY); //Check with prof about house keeping and early/late checkin-out
                
                System.out.print("\nReservation found. Do you want to check-in? (Y/N): ");
                String response = scanner.nextLine().trim();
                if (response.toLowerCase().equals("y")) {
                    List<RoomReservation> roomReservations = roomReservationSessionBeanRemote.retrieveRoomReservationsByReservation(reservation);
                    List<RoomReservation> allocatedRooms = new ArrayList<>();

                    for (RoomReservation roomReservation : roomReservations) {
                        if (roomReservation.getRoom() != null && roomReservation.getRoom().getRoomStatus() == RoomStatusEnum.ALLOCATED) {
                            allocatedRooms.add(roomReservation);
                        }
                    }

                    if (!allocatedRooms.isEmpty()) {
                        System.out.println("The following rooms are ready for check-in:");
                        for (RoomReservation allocatedRoom : allocatedRooms) {
                            System.out.println("Room Number: " + allocatedRoom.getRoom().getRoomNum());
                            //Update the room's status to OCCUPIED
                            allocatedRoom.getRoom().setRoomStatus(RoomStatusEnum.OCCUPIED);
                            roomSessionBeanRemote.updateRoom(allocatedRoom.getRoom());
                        }
                        
                        //Then update the reservation status to checked-in
                        reservation.setReservationStatusEnum(ReservationStatusEnum.CHECKED_IN);
                        reservationSessionBeanRemote.updateReservation(reservation);
                        System.out.println("You have successfully checked in to this reservation.");
                    } else { //If all rooms are not of the status 'ALLOCATED'
                        System.out.println("Your rooms are not ready yet. Unable to check you in."); 
                    }
                } else if (response.toLowerCase().equals("n")) {
                    System.out.println("\n");
                } else {
                    System.out.println("Invalid option. Please try again. \n");
                }
                
            } else {
                System.out.println("Unable to check-in. The reservation's check-in date is not today.");
            }

        } catch (Exception ex) {
            System.out.println("Error checking in: " + ex.getMessage());
        }
    }


    private void doCheckOutGuest() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Check-out guest ***\n");

            System.out.print("Enter reservation ID: ");
            Long reservationId = scanner.nextLong();
            scanner.nextLine();

            Reservation reservation = reservationSessionBeanRemote.getReservationByReservationId(reservationId);

            Date checkOutDate = stripTime(reservation.getCheckOutDate());
            Date today = stripTime(new Date());

            if (checkOutDate.equals(today)) {
                System.out.print("\nReservation found. Do you want to check-out? (Y/N): ");
                String response = scanner.nextLine().trim();

                if (response.toLowerCase().equals("y")) {
                    List<RoomReservation> roomReservations = roomReservationSessionBeanRemote.retrieveRoomReservationsByReservation(reservation);
                    List<RoomReservation> occupiedRooms = new ArrayList<>();

                    for (RoomReservation roomReservation : roomReservations) {
                        if (roomReservation.getRoom() != null && roomReservation.getRoom().getRoomStatus() == RoomStatusEnum.OCCUPIED) {
                            occupiedRooms.add(roomReservation);
                        }
                    }

                    if (!occupiedRooms.isEmpty()) {
                        System.out.println("The following rooms are being checked out:");
                        for (RoomReservation occupiedRoom : occupiedRooms) {
                            System.out.println("Room Number: " + occupiedRoom.getRoom().getRoomNum());
                            // Update the room's status to AVAILABLE
                            occupiedRoom.getRoom().setRoomStatus(RoomStatusEnum.AVAILABLE);
                            roomSessionBeanRemote.updateRoom(occupiedRoom.getRoom());
                        }

                        // Then update the reservation status to checked-out
                        reservation.setReservationStatusEnum(ReservationStatusEnum.CHECKED_OUT);
                        reservationSessionBeanRemote.updateReservation(reservation);
                        System.out.println("You have successfully checked out of this reservation.");
                    } else { // If rooms are not in the status 'OCCUPIED'
                        System.out.println("No rooms are currently occupied for this reservation.");
                    }
                } else if (response.toLowerCase().equals("n")) {
                    System.out.println("\n");
                } else {
                    System.out.println("Invalid option. Please try again.\n");
                }
            } else {
                System.out.println("Unable to check-out. The reservation's check-out date is not today.");
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

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n*** HoRS Management Client :: Guest Verification ***");
        System.out.println("Please select an option to verify your identity:");
        System.out.println("1: Verify by Email");
        System.out.println("2: Verify by Phone Number");
        System.out.println("3: Verify by Passport Number");
        System.out.println("4: Not a guest");

        int choice = 0;
        while (choice < 1 || choice > 3) {
            System.out.print("Enter your choice > ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline left-over

            switch (choice) {
                case 1:
                    System.out.print("Enter your email: ");
                    String email = scanner.nextLine().trim();
                    return verifyGuestByEmail(email);
                case 2:
                    System.out.print("Enter your phone number: ");
                    String phoneNumber = scanner.nextLine().trim();
                    return verifyGuestByPhoneNumber(phoneNumber);
                case 3:
                    System.out.print("Enter your passport number: ");
                    String passportNumber = scanner.nextLine().trim();
                    return verifyGuestByPassportNumber(passportNumber);
                case 4:
                    return null;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        return null;
    }

    private Guest verifyGuestByEmail(String email) {
        try {
            Guest guest = guestSessionBeanRemote.retrieveGuestByEmail(email);
            if (guest != null) {
                System.out.println("Guest verified successfully. Welcome, " + guest.getFirstName() + " " + guest.getLastName() + "!");
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
                System.out.println("Guest verified successfully. Welcome, " + guest.getFirstName() + " " + guest.getLastName() + "!");
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
                System.out.println("Guest verified successfully. Welcome, " + guest.getFirstName() + " " + guest.getLastName() + "!");
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

            System.out.println("Available Room Types:");
            for (int i = 0; i < roomTypes.size(); i++) {
                System.out.println(roomTypes.get(i).getTypeName());
            }

        } catch (Exception ex) {
            System.out.println("Error retrieving room types: " + ex.getMessage());
        }
    }

    private void displayRoomNumbers() {
        try {
            List<Room> rooms = roomSessionBeanRemote.retrieveAllRooms();

            System.out.println("Available Room Numbers:");
            for (int i = 0; i < rooms.size(); i++) {
                System.out.println((rooms.get(i).getRoomNum()));
            }

        } catch (Exception ex) {
            System.out.println("Error retrieving room numbers: " + ex.getMessage());
        }
    }
}
