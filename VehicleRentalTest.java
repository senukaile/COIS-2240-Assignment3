package assingment3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;




import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;



import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

class VehicleRentalTest {
    private RentalSystem rentalSystem;
    private Vehicle vehicle;
    private Customer customer;

    @BeforeEach
    void setUp() {
        rentalSystem = new RentalSystem();
        vehicle = new Car("Toyota", "Corolla", 2022, 5);
        vehicle.setLicensePlate("XYZ123");
        customer = new Customer(1, "John Doe");

        rentalSystem.addVehicle(vehicle);
        rentalSystem.addCustomer(customer);
    }

    @Test
    void testLicensePlateValidation() {
        Vehicle validVehicle1 = new Car("Toyota", "Camry", 2020, 5);
        Vehicle validVehicle2 = new Car("Honda", "Civic", 2019, 5);
        Vehicle validVehicle3 = new Car("Ford", "Focus", 2021, 5);

        validVehicle1.setLicensePlate("AAA100");
        validVehicle2.setLicensePlate("ABC567");
        validVehicle3.setLicensePlate("ZZZ999");

        assertEquals("AAA100", validVehicle1.getLicensePlate());
        assertEquals("ABC567", validVehicle2.getLicensePlate());
        assertEquals("ZZZ999", validVehicle3.getLicensePlate());

        Vehicle invalidVehicle = new Car("Tesla", "Model 3", 2022, 5);
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate("ZZZ99"));
    }

    @Test
    void testRentAndReturnVehicle() {
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus());

        rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 100.0);
        assertEquals(Vehicle.VehicleStatus.RENTED, vehicle.getStatus(), "Vehicle status should be RENTED.");

        rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 100.0);
        assertEquals(Vehicle.VehicleStatus.RENTED, vehicle.getStatus(), "Vehicle status should still be RENTED.");

        rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 50.0);
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus(), "Vehicle status should be AVAILABLE.");

        rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 50.0);
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus(), "Vehicle status should still be AVAILABLE.");
    }

    @Test
    void testSingletonRentalSystem() {
        try {
            Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
            
            int modifiers = constructor.getModifiers();
            assertTrue(Modifier.isPrivate(modifiers), "Constructor should be private to prevent direct instantiation.");

            constructor.setAccessible(true); 
            RentalSystem instance1 = constructor.newInstance();
            RentalSystem instance2 = RentalSystem.getInstance(); 

            assertSame(instance2, RentalSystem.getInstance(), "There should only be one instance of RentalSystem.");
            assertNotNull(instance2, "The instance should not be null.");

        } catch (Exception e) {
            fail("Exception occurred during Singleton validation test: " + e.getMessage());
        }
    }
}