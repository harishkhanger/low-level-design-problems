package lld.ridesharing.state;

import lld.ridesharing.model.Trip;
import lld.ridesharing.model.TripStatus;

public class InProgressState implements TripState{

    @Override
    public void start(Trip trip) {
        throw new IllegalStateException("Trip has already started");
    }

    @Override
    public void end(Trip trip) {
        trip.setFare(trip.computeFare());
        trip.freeDriver();
        trip.setState(new CompletedState());

    }

    @Override
    public void cancel(Trip trip) {
        trip.freeDriver();
        trip.setState(new CancelledState());
    }

    @Override
    public TripStatus status() {
        return TripStatus.IN_PROGRESS;
    }
}
