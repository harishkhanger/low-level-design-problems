package lld.elevator.state;

import lld.elevator.Direction;
import lld.elevator.Elevator;

public class MovingDownState implements ElevatorState {
    private final Elevator elevator;

    public MovingDownState(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public void step() {
        if (!elevator.hasDownStops()){
            if (elevator.hasUpStops()){
                elevator.setState(elevator.getMovingUpState());
            }
            return;
        }
        elevator.moveDown();
        if (elevator.getCurrentFloor() == elevator.peekDownStop()){
            elevator.pollDownStop();
            elevator.openDoors();
        }
        if (!elevator.hasDownStops()){
            if (elevator.hasUpStops()){
                elevator.setState(elevator.getMovingUpState());
            }else {
                elevator.setState(elevator.getIdleState());
            }
        }
    }

    @Override
    public Direction getDirection() {
        return Direction.DOWN;
    }
}
