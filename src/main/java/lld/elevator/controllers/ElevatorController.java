package lld.elevator.controllers;

import lld.elevator.Direction;
import lld.elevator.Elevator;
import lld.elevator.strategy.DispatchStrategy;

import java.util.ArrayList;
import java.util.List;

public class ElevatorController {
    private final List<Elevator> elevatorList;
    private final DispatchStrategy dispatchStrategy;

    public ElevatorController(List<Elevator> elevators, DispatchStrategy dispatchStrategy) {
        this.elevatorList = new ArrayList<>(elevators);
        this.dispatchStrategy = dispatchStrategy;
    }

    public Elevator requestElevator (int floor, Direction direction) {
        var chosen = dispatchStrategy.selectElevator(elevatorList, floor, direction);
        chosen.addStop(floor);
        return chosen;
    }

    public void step(){
        elevatorList.forEach(Elevator::step);
    }

    public List<Elevator> getElevatorList(){
        return List.copyOf(elevatorList);
    }
}
