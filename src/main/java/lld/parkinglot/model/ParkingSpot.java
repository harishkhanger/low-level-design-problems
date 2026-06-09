package lld.parkinglot.model;

public class ParkingSpot {
    private final int id;
    private final SpotSize spotSize;
    private Vehicle currentVehicle;

    public ParkingSpot(int id, SpotSize spotSize) {
        this.id = id;
        this.spotSize = spotSize;
    }

    public boolean isAvailable() {
        return currentVehicle == null;
    }

    public void parkVehicle(Vehicle vehicle) {
        if (currentVehicle != null) throw new IllegalArgumentException("Parking Spot is already Occupied");
        if (canFit(vehicle)){
            this.currentVehicle = vehicle;
        }else {
            throw new IllegalArgumentException("Can not park the provided vehicle on the current spot");
        }
    }

    public void removeVehicle(){
        if (isAvailable())throw new IllegalArgumentException("No vehicle is parked here");
        this.currentVehicle = null;
    }

    public boolean canFit(Vehicle vehicle) {
        return this.spotSize.getValue() >= vehicle.getVehicleType().getRequiredSize().getValue();
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public int getId() {
        return id;
    }

    public SpotSize getSpotSize() {
        return spotSize;
    }
}
