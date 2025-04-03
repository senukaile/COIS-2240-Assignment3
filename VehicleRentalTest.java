package assingment3;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class VehicleRentalTest {
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
}
