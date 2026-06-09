package lld.parkinglot.model;

import java.time.LocalDateTime;

public record Ticket(String ticketId, Vehicle vehicle, ParkingSpot spot, LocalDateTime entryTime) {
}