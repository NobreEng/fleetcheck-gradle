package pt.upt.fleetcheck;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FleetServiceTest {

    @Test
    void vehicleExactlyAtServiceIntervalNeedsService() {
        Vehicle v = new Vehicle("V3", "Diesel", 65000, 55000, 10000);
        assertTrue(new FleetService().needsService(v));
    }
}
