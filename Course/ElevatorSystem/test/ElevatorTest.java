import org.junit.jupiter.api.Test;
import org.togetherjava.event.elevator.elevators.Elevator;
import org.togetherjava.event.elevator.elevators.ElevatorPanel;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class ElevatorTest {
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
