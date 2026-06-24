package lld.ridesharing.state;

import lld.ridesharing.model.Trip;
import lld.ridesharing.model.TripStatus;

public class CancelledState implements TripState{

    @Override
    public void start(Trip trip) {
        thISE();
    }

    @Override
    public void end(Trip trip) {
        thISE();
    }

    @Override
    public void cancel(Trip trip) {
        thISE();
    }

    @Override
    public TripStatus status() {
        return TripStatus.CANCELLED;
    }

    private static void thISE() {
        throw new IllegalStateException("Trip was cancelled");
    }

}
