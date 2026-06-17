package lld.elevator.state;

import lld.elevator.Direction;
import lld.elevator.Elevator;

public class IdleState implements ElevatorState {

    private final Elevator elevator;

    public IdleState(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public void step() {
    }

    @Override
    public Direction getDirection() {
        return Direction.IDLE;
    }
}
