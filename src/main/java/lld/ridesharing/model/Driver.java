package lld.ridesharing.model;

public class Driver {
    private final String id;
    private final String name;
    private Location location;
    private DriverStatus driverStatus;

    public Driver(String id, String name, Location location, DriverStatus driverStatus){
        this.id = id;
        this.name = name;
        this.location = location;
        this.driverStatus = driverStatus;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public DriverStatus getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(DriverStatus driverStatus) {
        this.driverStatus = driverStatus;
    }
}
