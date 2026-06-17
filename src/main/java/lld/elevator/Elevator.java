package lld.elevator;

import lld.elevator.state.ElevatorState;
import lld.elevator.state.IdleState;
import lld.elevator.state.MovingDownState;
import lld.elevator.state.MovingUpState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Elevator {

    private final int id;
    private int currentFloor;

    private final PriorityQueue<Integer> upStops = new PriorityQueue<>();
    private final PriorityQueue<Integer> downStops = new PriorityQueue<>(Comparator.reverseOrder());


    private final ElevatorState idleState;
    private final ElevatorState movingUpState;
    private final ElevatorState movingDownState;
    private ElevatorState currentState;

    private final List<Integer> visitedFloors = new ArrayList<>();

    public Elevator(int id, int startFloor) {
        this.id = id;
        this.currentFloor = startFloor;
        this.idleState = new IdleState(this);
        this.movingUpState = new MovingUpState(this);
        this.movingDownState = new MovingDownState(this);
        this.currentState = idleState;
    }

    public void addStop(int floor) {
        if (floor == currentFloor) {
            openDoors();
            return;
        }
        if (floor > currentFloor) {
            upStops.add(floor);
        } else {
            downStops.add(floor);
        }
        if (currentState == idleState) {
            setState(floor > currentFloor ? movingUpState : movingDownState);
        }
    }

    public void step() {
        currentState.step();
    }

    public void setState(ElevatorState state) { this.currentState = state; }
    public ElevatorState getCurrentState() { return currentState; }
    public ElevatorState getIdleState() { return idleState; }
    public ElevatorState getMovingUpState() { return movingUpState; }
    public ElevatorState getMovingDownState() { return movingDownState; }

    public void moveUp() { currentFloor++; }
    public void moveDown() { currentFloor--; }

    public boolean hasUpStops() { return !upStops.isEmpty(); }
    public boolean hasDownStops() { return !downStops.isEmpty(); }
    public int peekUpStop() { return upStops.peek(); }
    public int peekDownStop() { return downStops.peek(); }
    public void pollUpStop() { upStops.poll(); }
    public void pollDownStop() { downStops.poll(); }


    public void openDoors() { visitedFloors.add(currentFloor); }

    public int getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public Direction getDirection() { return currentState.getDirection(); }
    public List<Integer> getVisitedFloors() { return List.copyOf(visitedFloors); }
}
