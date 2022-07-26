import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.togetherjava.event.elevator.elevators.Elevator;
import org.togetherjava.event.elevator.elevators.ElevatorPanel;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PreviousElevatorTest {
    @Test
    void testGetFloors() {
        int expectedMinFloor = 3;
        int expectedFloorsServed = 4;
        int expectedCurrentFloor = 5;
        Elevator elevator =
                new Elevator(expectedMinFloor, expectedFloorsServed, expectedCurrentFloor);

        int actualMinFloor = elevator.getMinFloor();
        int actualFloorsServed = elevator.getFloorsServed();
        int actualCurrentFloor = elevator.getCurrentFloor();

        assertEquals(expectedMinFloor, actualMinFloor,
                "The getMinFloor() method of Elevator needs to return the min floor given in the constructor.");

        assertEquals(expectedFloorsServed, actualFloorsServed,
                "The getFloorsServed() method of Elevator needs to return the served floors given in the constructor.");

        assertEquals(expectedCurrentFloor, actualCurrentFloor,
                "The getCurrentFloor() method of Elevator needs to return the current floor given in the constructor.");
    }

    @Test
    void testGetId() {
        int anyMinFloor = 3;
        int anyFloorsServed = 4;
        int anyCurrentFloor = 5;

        Set<Integer> elevatorIds = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            Elevator elevator = new Elevator(anyMinFloor, anyFloorsServed, anyCurrentFloor);
            int id = elevator.getId();

            assertFalse(elevatorIds.contains(id),
                    "The IDs of elevators must be unique. Found a collision with ID %d when generating a few of them.".formatted(
                            id));
            elevatorIds.add(id);
        }
    }

    @Test
    void testImplementsElevatorPanel() {
        assertTrue(ElevatorPanel.class.isAssignableFrom(Elevator.class),
                "The Elevator class is supposed to implement the ElevatorPanel interface.");
    }

    @Test
    void testMoveOneFloor() {
        boolean hasMethod = Arrays.stream(Elevator.class.getMethods())
                .map(Method::getName)
                .anyMatch("moveOneFloor"::equals);

        assertTrue(hasMethod, "The Elevator class is supposed to have a 'void moveOneFloor()' method.");
    }
}
