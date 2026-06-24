package lld.ridesharing.service;

import lld.ridesharing.model.*;
import lld.ridesharing.strategy.MatchingStrategy;
import lld.ridesharing.strategy.PricingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RideService {
    private final List<Driver> driverList = new ArrayList<>();
    private final MatchingStrategy matchingStrategy;
    private final PricingStrategy pricingStrategy;
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    public RideService(MatchingStrategy matchingStrategy, PricingStrategy pricingStrategy) {
        this.matchingStrategy = matchingStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    public synchronized void registerDriver(Driver driver) {
        driverList.add(driver);
    }

    public synchronized Trip requestTrip(Rider rider, Location pickUp, Location drop) {
        List<Driver> available = driverList.stream().filter(a -> a.getDriverStatus() == DriverStatus.AVAILABLE).toList();
        Driver driver = matchingStrategy.findDriver(available, pickUp).orElseThrow(() -> new IllegalStateException("Driver not available"));
        driver.setDriverStatus(DriverStatus.ON_TRIP);
        return new Trip("T" + atomicInteger.incrementAndGet(), rider, driver, pickUp, drop, pricingStrategy);
    }
}
