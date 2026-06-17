package lld.elevator.strategy;

import lld.elevator.Direction;
import lld.elevator.Elevator;

import java.util.List;

public interface DispatchStrategy {
    Elevator selectElevator(List<Elevator> elevators, int floor, Direction direction);
}
