package lld.ridesharing.strategy;

import lld.ridesharing.model.Location;

public interface PricingStrategy {
    double calculateFare(Location pickUp, Location drop);
}
