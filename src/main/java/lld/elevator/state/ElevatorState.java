package lld.elevator.state;

import lld.elevator.Direction;

public interface ElevatorState {
    void step();
    Direction getDirection();
}
