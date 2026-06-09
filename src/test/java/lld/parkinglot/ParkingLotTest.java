package lld.parkinglot;

import lld.parkinglot.model.ParkingFloor;
import lld.parkinglot.model.ParkingSpot;
import lld.parkinglot.model.SpotSize;
import lld.parkinglot.model.Ticket;
import lld.parkinglot.model.Vehicle;
import lld.parkinglot.model.VehicleType;
import lld.parkinglot.service.ParkingLot;
import lld.parkinglot.strategy.FlatFeeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingLotTest {

    private ParkingLot lot;
    private MutableClock clock;

    @BeforeEach
    void setUp() {
        List<ParkingSpot> spots = List.of(
                new ParkingSpot(1, SpotSize.SMALL),
                new ParkingSpot(2, SpotSize.MEDIUM),
                new ParkingSpot(3, SpotSize.LARGE));
        ParkingFloor floor = new ParkingFloor(1, spots);
        clock = new MutableClock(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC);
        lot = new ParkingLot(List.of(floor), new FlatFeeStrategy(20), clock);
    }

    private Vehicle car(String plate) {
        return new Vehicle(plate, VehicleType.CAR);
    }

    private Vehicle motorcycle(String plate) {
        return new Vehicle(plate, VehicleType.MOTOR_CYCLE);
    }

    @Test
    @DisplayName("Parking a vehicle returns a ticket referencing that vehicle")
    void parkReturnsTicket() {
        Optional<Ticket> ticket = lot.parkVehicle(car("KA-01-1111"));

        assertTrue(ticket.isPresent());
        assertEquals("KA-01-1111", ticket.get().vehicle().getNumber());
    }

    @Test
    @DisplayName("Fee is charged per started hour based on parked duration")
    void feeIsChargedByDuration() {
        Ticket ticket = lot.parkVehicle(car("KA-01-2222")).orElseThrow();

        clock.advance(Duration.ofHours(3));
        double fee = lot.unparkVehicle(ticket.ticketId());

        assertEquals(60.0, fee, 0.001);
    }

    @Test
    @DisplayName("A partial hour is rounded up to a full hour")
    void partialHourRoundsUp() {
        Ticket ticket = lot.parkVehicle(car("KA-01-3333")).orElseThrow();

        clock.advance(Duration.ofMinutes(65));
        double fee = lot.unparkVehicle(ticket.ticketId());

        assertEquals(40.0, fee, 0.001);
    }

    @Test
    @DisplayName("When every spot is taken, parking returns empty (lot full)")
    void lotFullReturnsEmpty() {
        assertTrue(lot.parkVehicle(motorcycle("M-1")).isPresent());
        assertTrue(lot.parkVehicle(motorcycle("M-2")).isPresent());
        assertTrue(lot.parkVehicle(motorcycle("M-3")).isPresent());

        Optional<Ticket> fourth = lot.parkVehicle(motorcycle("M-4"));
        assertFalse(fourth.isPresent());
    }

    @Test
    @DisplayName("Best-fit: a car takes the MEDIUM spot, not the LARGE one")
    void bestFitPicksSmallestSpot() {
        Ticket ticket = lot.parkVehicle(car("KA-01-4444")).orElseThrow();

        assertEquals(SpotSize.MEDIUM, ticket.spot().getSpotSize());
    }

    @Test
    @DisplayName("Freeing a spot lets the next vehicle reuse it")
    void spotIsReusedAfterUnpark() {
        Ticket first = lot.parkVehicle(motorcycle("M-1")).orElseThrow();
        lot.parkVehicle(motorcycle("M-2"));
        lot.parkVehicle(motorcycle("M-3"));
        assertFalse(lot.parkVehicle(motorcycle("M-4")).isPresent());  // full

        lot.unparkVehicle(first.ticketId());

        assertTrue(lot.parkVehicle(motorcycle("M-5")).isPresent());
    }

    @Test
    @DisplayName("Unparking with an unknown ticket id throws")
    void invalidTicketThrows() {
        assertThrows(IllegalArgumentException.class, () -> lot.unparkVehicle("does-not-exist"));
    }

    @Test
    @DisplayName("Unparking the same ticket twice throws the second time")
    void doubleUnparkThrows() {
        Ticket ticket = lot.parkVehicle(car("KA-01-5555")).orElseThrow();
        clock.advance(Duration.ofHours(1));

        lot.unparkVehicle(ticket.ticketId());
        assertThrows(IllegalArgumentException.class,
                () -> lot.unparkVehicle(ticket.ticketId()));
    }
}
