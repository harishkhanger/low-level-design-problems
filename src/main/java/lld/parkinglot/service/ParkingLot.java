package lld.parkinglot.service;

import lld.parkinglot.model.ParkingFloor;
import lld.parkinglot.model.ParkingSpot;
import lld.parkinglot.model.Ticket;
import lld.parkinglot.model.Vehicle;
import lld.parkinglot.strategy.FeeStrategy;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingLot {
    private final List<ParkingFloor> parkingFloors;
    private final FeeStrategy feeStrategy;
    private final Clock clock;
    private final Map<String, Ticket> activeTickets;

    public ParkingLot(List<ParkingFloor> parkingFloors, FeeStrategy feeStrategy, Clock clock) {
        this.parkingFloors = parkingFloors;
        this.feeStrategy = feeStrategy;
        this.clock = clock;
        activeTickets = new HashMap<>();
    }


    public Optional<Ticket> parkVehicle(Vehicle vehicle) {
        for (ParkingFloor parkingFloor : parkingFloors) {
            Optional<ParkingSpot> parkingSpotOptional = parkingFloor.findAvailableSpot(vehicle);
            if (parkingSpotOptional.isPresent()) {
                String id = UUID.randomUUID().toString();
                Ticket ticket = new Ticket(id, vehicle, parkingSpotOptional.get(), LocalDateTime.now(clock));
                parkingSpotOptional.get().parkVehicle(vehicle);
                activeTickets.put(id, ticket);
                return Optional.of(ticket);
            }
        }
        return Optional.empty();
    }

    public double unparkVehicle(String ticketId) {
        Ticket ticket = activeTickets.get(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Invalid ticket");
        }
        ParkingSpot parkingSpot = ticket.spot();
        parkingSpot.removeVehicle();
        activeTickets.remove(ticketId);
        return feeStrategy.calculateFee(ticket, LocalDateTime.now(clock));
    }
}
