package lld.ridesharing.strategy;

import lld.ridesharing.model.Driver;
import lld.ridesharing.model.Location;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NearestDriverStrategy implements MatchingStrategy{

    @Override
    public Optional<Driver> findDriver(List<Driver> driverList, Location pickUp) {
        return driverList.stream().min(Comparator.comparingDouble(d->d.getLocation().distanceTo(pickUp)));
    }
}
