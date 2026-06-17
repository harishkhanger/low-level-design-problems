package lld.elevator.state;

import lld.elevator.Direction;
import lld.elevator.Elevator;

public class MovingUpState implements ElevatorState {
    private final Elevator elevator;

    public MovingUpState(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public void step() {
        if (!elevator.hasUpStops()){
            if (elevator.hasDownStops()){
                elevator.setState(elevator.getMovingDownState());
            }
            return;
        }
        elevator.moveUp();
        if (elevator.getCurrentFloor() == elevator.peekUpStop()){
            elevator.pollUpStop();
            elevator.openDoors();
        }
        if (!elevator.hasUpStops()){
            if (elevator.hasDownStops()){
                elevator.setState(elevator.getMovingDownState());
            }else {
                elevator.setState(elevator.getIdleState());
            }
        }
    }

    @Override
    public Direction getDirection() {
        return Direction.UP;
    }
}
