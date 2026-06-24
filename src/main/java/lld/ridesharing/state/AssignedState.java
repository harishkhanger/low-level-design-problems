package lld.ridesharing.state;

import lld.ridesharing.model.Trip;
import lld.ridesharing.model.TripStatus;

public class AssignedState implements TripState {

    @Override
    public void start(Trip trip) {
        trip.setState(new InProgressState());
    }

    @Override
    public void end(Trip trip) {
        throw new IllegalStateException("Trip hasn't started yet");
    }

    @Override
    public void cancel(Trip trip) {
        trip.freeDriver();
        trip.setState(new CancelledState());
    }

    @Override
    public TripStatus status() {
        return TripStatus.ASSIGNED;
    }
}
