package lld.ridesharing.strategy;

import lld.ridesharing.model.Location;

public class DistanceBasedPricing implements PricingStrategy{

    private final double base;
    private final double perK;

    public DistanceBasedPricing (double base, double perK){
        this.base = base;
        this.perK = perK;
    }

    @Override
    public double calculateFare(Location pickUp, Location drop) {
        return base+perK * pickUp.distanceTo(drop);
    }
}
