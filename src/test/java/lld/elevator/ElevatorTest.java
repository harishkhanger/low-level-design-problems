package lld.elevator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElevatorTest {

    private void runUntilIdle(Elevator e) {
        int safety = 1000;
        while (e.getDirection() != Direction.IDLE && safety-- > 0) {
            e.step();
        }
    }

    @Test
    @DisplayName("Moves up one floor per step and stops at the target")
    void movesUpToStop() {
        Elevator e = new Elevator(1, 0);
        e.addStop(3);
        runUntilIdle(e);

        assertEquals(3, e.getCurrentFloor());
        assertEquals(List.of(3), e.getVisitedFloors());
        assertEquals(Direction.IDLE, e.getDirection());
    }

    @Test
    @DisplayName("LOOK: serves all stops going up, then reverses for stops below")
    void looksUpThenDown() {
        Elevator e = new Elevator(1, 5);
        e.addStop(7);
        e.addStop(3);
        runUntilIdle(e);

        assertEquals(List.of(7, 3), e.getVisitedFloors());
        assertEquals(3, e.getCurrentFloor());
    }

    @Test
    @DisplayName("LOOK: picks up a newly added stop that lies on the current path")
    void picksUpStopOnTheWay() {
        Elevator e = new Elevator(1, 0);
        e.addStop(5);
        e.step();
        e.step();
        e.addStop(3);
        runUntilIdle(e);

        assertEquals(List.of(3, 5), e.getVisitedFloors());   // 3 served before 5
    }

    @Test
    @DisplayName("Going down, the nearest floor below is served first")
    void downStopsNearestFirst() {
        Elevator e = new Elevator(1, 10);
        e.addStop(4);
        e.addStop(7);
        runUntilIdle(e);

        assertEquals(List.of(7, 4), e.getVisitedFloors());
    }

    @Test
    @DisplayName("Stepping an idle elevator does nothing")
    void idleStepIsNoOp() {
        Elevator e = new Elevator(1, 2);
        e.step();

        assertEquals(2, e.getCurrentFloor());
        assertEquals(Direction.IDLE, e.getDirection());
        assertTrue(e.getVisitedFloors().isEmpty());
    }
}
