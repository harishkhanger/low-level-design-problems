package lld.elevator.strategy;

import lld.elevator.Direction;
import lld.elevator.Elevator;

import java.util.Comparator;
import java.util.List;

public class NearestDispatchStrategy implements DispatchStrategy {

    @Override
    public Elevator selectElevator(List<Elevator> elevators, int floor, Direction direction) {
        return elevators.stream().min(Comparator.comparingInt(a -> Math.abs(a.getCurrentFloor() - floor))).orElseThrow();
    }
}
