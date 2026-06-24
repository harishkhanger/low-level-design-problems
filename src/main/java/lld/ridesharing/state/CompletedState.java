package lld.ridesharing.state;

import lld.ridesharing.model.Trip;
import lld.ridesharing.model.TripStatus;

public class CompletedState implements TripState {

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
        return TripStatus.COMPLETED;
    }

    private static void thISE() {
        throw new IllegalStateException("Trip already completed");
    }
}
