package lld.parkinglot.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ParkingFloor {
    private final int id;
    private final List<ParkingSpot> parkingSpots;

    public ParkingFloor(int id, List<ParkingSpot> parkingSpots) {
        this.id = id;
        this.parkingSpots = new ArrayList<>(parkingSpots);
        this.parkingSpots.sort(Comparator.comparingInt(a -> a.getSpotSize().getValue()));
    }

    public Optional<ParkingSpot> findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot parkingSpot : parkingSpots) {
            if (parkingSpot.isAvailable() && parkingSpot.canFit(vehicle)) {
                return Optional.of(parkingSpot);
            }
        }
        return Optional.empty();
    }

    public int getId() {
        return id;
    }
}
