package lld.ridesharing.state;

import lld.ridesharing.model.Trip;
import lld.ridesharing.model.TripStatus;

public interface TripState {
    void start(Trip trip);
    void end(Trip trip);
    void cancel(Trip trip);
    TripStatus status();
}
