/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ExceptionReportSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import entity.Room;
import entity.RoomType;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.PartnerAccessRightEnum;
import util.enumeration.RoomTypeStatusEnum;
import util.exception.InvalidAccountCredentialsException;
import util.exception.InvalidLoginCredentialsException;
import util.exception.InvalidRoomTypeTierNumberException;
import util.exception.InvalidRoomDetailsException;
import util.exception.InvalidRoomTypeCapacityException;
import util.exception.InvalidRoomTypeDetailsException;

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
            RoomRateSessionBeanRemote roomRateSessionBeanRemote
    ) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.exceptionReportSessionBeanRemote = exceptionReportSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
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
            //Because the current logged in employee will also be shown
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
            Integer capacity = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter room type amenities: ");
            String amenities = scanner.nextLine().trim();

            System.out.print("Enter room type tier number: ");
            Integer tierNumber = scanner.nextInt();
            scanner.nextLine();

            RoomType newRoomType = new RoomType(name, size, bed, description, capacity, amenities, tierNumber);

            roomTypeSessionBeanRemote.createNewRoomType(newRoomType);

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
            System.out.println("Number of available rooms left: " + roomType.getNumOfAvailRooms());
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
            System.out.println("Number of available rooms left: " + existingRoomType.getNumOfAvailRooms());
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
            System.out.println("Number of available rooms left: " + existingRoomType.getNumOfAvailRooms());
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
                            roomType.getNumOfAvailRooms(),
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

    private void doCreateNewRoom() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Create new room ***\n");

            System.out.print("Enter room type: ");
            String roomTypeString = scanner.nextLine().trim();

            RoomType existingRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByName(roomTypeString);

            System.out.print("Enter floor number: ");
            String floorNum = scanner.nextLine().trim();

            System.out.print("Enter sequence number: ");
            String seqNum = scanner.nextLine().trim();

            //If all inputs are filled in
            if (floorNum.length() > 0 && seqNum.length() > 0) {
                Room newRoom = new Room(floorNum, seqNum, existingRoomType);
                roomSessionBeanRemote.createNewRoom(newRoom);
            } else {
                throw new InvalidRoomDetailsException("Invalid room details");
            }

        System.out.println("New room for '" + roomTypeString + "' has successfully been created.");
    }
    catch (Exception ex) {
            System.out.println("Error creating new room: " + ex.getMessage() + "\n");
    }
}

private void doUpdateRoom() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Update room ***\n");
            
            //Logic to update room (NOT DONE YET)
            
        } catch (Exception ex) {
            System.out.println("Error updating room: " + ex.getMessage() + "\n");
        }
        
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Update room ***\n");

            System.out.print("Enter room number: ");
            String roomNumString = scanner.nextLine().trim();

            Room existingRoom = roomSessionBeanRemote.retrieveRoomByRoomNum(roomNumString);

            System.out.println("\nRoom type found. Details for room type '" + existingRoomType.getTypeName() + "': ");
            System.out.println("Size: " + existingRoomType.getSize());
            System.out.println("Bed: " + existingRoomType.getBed());
            System.out.println("Description: " + existingRoomType.getDescription());
            System.out.println("Total capacity: " + existingRoomType.getCapacity());
            System.out.println("Number of available rooms left: " + existingRoomType.getNumOfAvailRooms());
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
    
    private void doDeleteRoom() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** HoRS Management Client :: Delete room ***\n");
            
            //Logic to delete room (NOT DONE YET)
            
        } catch (Exception ex) {
            System.out.println("Error deleting room: " + ex.getMessage() + "\n");
        }
    }
    
    private void doViewAllRooms() {
        try {
            System.out.println("*** HoRS Management Client :: View all rooms ***\n");
            System.out.printf("%-5s %-20s %-20s %-20s\n", 
                              "No.", "Room number", "Room type", "Status");
            int index = 1;
            
            //Logic to view all rooms (NOT DONE YET)
            
            
        } catch (Exception ex) {
            System.out.println("Error viewing all rooms: " + ex.getMessage() + "\n");
        }
    }
    
    
    private void doViewRoomAllocationExceptionReport() {
        //Logic to view room allocation exception report (NOT DONE YET)
    }
    
    
    /** ALL SALES MANAGER METHODS **/
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
    
    private void doCreateNewRoomRate() {
        
    }
    
    private void doViewRoomRateDetails() {
        
    }
    
    private void doUpdateRoomRate() {
        
    }
    
    private void doDeleteRoomRate() {
        
    }
    
    private void doViewAllRoomRates() {
        
    }
    
    
    /** ALL GUEST RELATION OFFICER METHODS **/
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
                    }  else if (response == 5) { //Since before this is the first screen, can just do a break
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
            System.out.println("Error showing guest relation officer menu: " + ex.getMessage() + "\n");
        }

    }
   
    private void doWalkInSearchRoom() {
        
    }
    
    private void doWalkInReserveRoom() {
        
    }
    
    private void doCheckInGuest() {
        
    }
    
    private void doCheckOutGuest() {
        
    }            
    
 
    
    
    /** HELPER METHOD **/
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
    
    
    
}
