package lld.elevator;

import lld.elevator.controllers.ElevatorController;
import lld.elevator.strategy.NearestDispatchStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElevatorControllerTest {

    private void runUntilAllIdle(ElevatorController controller) {
        int safety = 1000;
        while (safety-- > 0) {
            boolean anyMoving = controller.getElevatorList().stream()
                    .anyMatch(e -> e.getDirection() != Direction.IDLE);
            if (!anyMoving) return;
            controller.step();
        }
    }

    @Test
    @DisplayName("A hall call is dispatched to the nearest elevator")
    void dispatchesToNearest() {
        Elevator e1 = new Elevator(1, 0);
        Elevator e2 = new Elevator(2, 10);
        ElevatorController controller =
                new ElevatorController(List.of(e1, e2), new NearestDispatchStrategy());

        Elevator chosen = controller.requestElevator(8, Direction.UP);

        assertEquals(2, chosen.getId());
    }

    @Test
    @DisplayName("Full flow: a hall call plus a car call are served in LOOK order")
    void servesHallThenCarCall() {
        Elevator e = new Elevator(1, 0);
        ElevatorController controller =
                new ElevatorController(List.of(e), new NearestDispatchStrategy());

        Elevator car = controller.requestElevator(5, Direction.UP);
        car.addStop(9);

        runUntilAllIdle(controller);

        assertEquals(List.of(5, 9), e.getVisitedFloors());
        assertEquals(9, e.getCurrentFloor());
    }

    @Test
    @DisplayName("Each tick advances every elevator that has work")
    void stepAdvancesElevators() {
        Elevator e1 = new Elevator(1, 0);
        Elevator e2 = new Elevator(2, 10);
        ElevatorController controller =
                new ElevatorController(List.of(e1, e2), new NearestDispatchStrategy());

        e1.addStop(2);
        e2.addStop(8);

        controller.step();

        assertEquals(1, e1.getCurrentFloor());
        assertEquals(9, e2.getCurrentFloor());
    }
}
