import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import assingment3.RentalRecord;



public class RentalSystem {
    private static RentalSystem instance;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    RentalSystem() {
        loadData(); 
    }

    
    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Error: A vehicle with this license plate already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        System.out.println("Vehicle added successfully.");
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Error: A customer with this ID already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        System.out.println("Customer added successfully.");
        return true;
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    public void displayAvailableVehicles() {
        System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
        System.out.println("---------------------------------------------------------------------------------");

        for (Vehicle v : vehicles) {
            if (v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println("|     " + (v instanceof Car ? "Car          " : "Motorcycle   ") + "|\t" + v.getLicensePlate() + "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|");
            }
        }
        System.out.println();
    }

    public void displayAllVehicles() {
        for (Vehicle v : vehicles) {
            System.out.println("  " + v.getInfo());
        }
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }

    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }

    public Customer findCustomerByName(String name) {
        for (Customer c : customers)
            if (c.getCustomerName().equalsIgnoreCase(name))
                return c;
        return null;
    }

    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRentalRecords();
    }

    private void loadVehicles() {
        try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) { 
                String[] parts = line.split(",");
                
                if (parts.length < 5) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                String licensePlate = parts[0]; 
                String make = capitalize(parts[1]); 
                String model = capitalize(parts[2]); 
                int year = Integer.parseInt(parts[3]); 
                String type = parts[4]; 

                Vehicle vehicle = type.equalsIgnoreCase("Car") ? 
                    new Car(make, model, year, 5) : 
                    new Motorcycle(make, model, year, false);

                vehicle.setLicensePlate(licensePlate); 
                vehicles.add(vehicle); 
            } 
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number in vehicles file: " + e.getMessage());
        }
    }


    private void loadCustomers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int customerId = Integer.parseInt(parts[0]);
                String customerName = parts[1];
                Customer customer = new Customer(customerId, customerName);
                customers.add(customer);
            }
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadRentalRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String licensePlate = parts[0];
                int customerId = Integer.parseInt(parts[1]);
                LocalDate date = LocalDate.parse(parts[2]);
                double amount = Double.parseDouble(parts[3]);
                String action = parts[4];

                Vehicle vehicle = findVehicleByPlate(licensePlate);
                Customer customer = findCustomerById(customerId);
                
                RentalRecord record = new RentalRecord(vehicle, customer, date, amount, action);
                rentalHistory.addRecord(record);
            }
        } catch (IOException e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        }
    }

    private void saveVehicle(Vehicle vehicle) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt", true))) {
            writer.write(vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", true))) {
            writer.write(customer.getCustomerId() + "," + customer.getCustomerName());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("rental_records.txt", true))) {
            Vehicle vehicle = record.getVehicle(); 
            Customer customer = record.getCustomer(); 
            
            String type = (vehicle instanceof Car) ? "Car" : "Motorcycle";
            
           
            writer.write(vehicle.getLicensePlate() + "," 
                         + customer.getCustomerId() + "," 
                         + record.getRecordDate() + ","   
                         + record.getTotalAmount() + "," 
                         + record.getRecordType() + ","  
                         + type);

            writer.newLine(); 
        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }


    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    
    
    
}