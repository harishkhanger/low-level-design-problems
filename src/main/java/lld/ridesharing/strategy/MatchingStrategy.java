package lld.ridesharing.strategy;

import lld.ridesharing.model.Driver;
import lld.ridesharing.model.Location;

import java.util.List;
import java.util.Optional;

public interface MatchingStrategy {
    Optional<Driver> findDriver(List<Driver> driverList, Location pickUp);
}
