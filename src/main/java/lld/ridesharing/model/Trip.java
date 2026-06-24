package lld.ridesharing.model;

import lld.ridesharing.state.AssignedState;
import lld.ridesharing.state.TripState;
import lld.ridesharing.strategy.PricingStrategy;


public class Trip {

    private final String id;
    private final Rider rider;
    private final Driver driver;
    private final Location pickup;
    private final Location destination;
    private final PricingStrategy pricingStrategy;

    private double fare;
    private TripState currentState;

    public Trip(String id, Rider rider, Driver driver, Location pickup,
                Location destination, PricingStrategy pricingStrategy) {
        this.id = id;
        this.rider = rider;
        this.driver = driver;
        this.pickup = pickup;
        this.destination = destination;
        this.pricingStrategy = pricingStrategy;
        this.currentState = new AssignedState();
    }

    public void start() { currentState.start(this); }
    public void end() { currentState.end(this); }
    public void cancel() { currentState.cancel(this); }

    public void setState(TripState state) { this.currentState = state; }

    public void freeDriver() { driver.setDriverStatus(DriverStatus.AVAILABLE); }

    public double computeFare() { return pricingStrategy.calculateFare(pickup, destination); }

    public void setFare(double fare) { this.fare = fare; }

    public TripStatus getStatus() { return currentState.status(); }
    public String getId() { return id; }
    public Rider getRider() { return rider; }
    public Driver getDriver() { return driver; }
    public Location getPickup() { return pickup; }
    public Location getDestination() { return destination; }
    public double getFare() { return fare; }
}
