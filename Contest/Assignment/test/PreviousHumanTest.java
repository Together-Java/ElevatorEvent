import org.junit.jupiter.api.Test;
import org.togetherjava.event.elevator.humans.ElevatorListener;
import org.togetherjava.event.elevator.humans.Human;

import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PreviousHumanTest {
    @Test
    void testStateEnum() {
        Set<String> actualNames = Arrays.stream(Human.State.values())
                .map(Objects::toString)
                .collect(Collectors.toSet());

        Set<String> expectedNames =
                Set.of("IDLE", "WAITING_FOR_ELEVATOR", "TRAVELING_WITH_ELEVATOR", "ARRIVED");

        assertEquals(expectedNames, actualNames,
                "The names of the enum values for 'Human.State' do not match. Please check that the spelling is exactly the same.");
    }

    @Test
    void testGetCurrentState() {
        int anyStartingFloor = 1;
        int anyDestinationFloor = 3;
        Human human = new Human(anyStartingFloor, anyDestinationFloor);

        Human.State currentState = human.getCurrentState();

        assertEquals("IDLE", currentState.name(),
                "The getCurrentState() method of Human needs to return the current state, which is supposed to be IDLE for now.");
    }

    @Test
    void testGetFloors() {
        int expectedStartingFloor = 1;
        int expectedDestinationFloor = 3;
        Human human = new Human(expectedStartingFloor, expectedDestinationFloor);

        int actualStartingFloor = human.getStartingFloor();
        int actualDestinationFloor = human.getDestinationFloor();

        assertEquals(expectedStartingFloor, actualStartingFloor,
                "The getStartingFloor() method of Human needs to return the starting floor given in the constructor.");

        assertEquals(expectedDestinationFloor, actualDestinationFloor,
                "The getDestinationFloor() method of Human needs to return the destination floor given in the constructor.");
    }

    @Test
    void testSameFloorAllowed() {
        int expectedStartingAndDestinationFloor = 2;
        Human human =
                new Human(expectedStartingAndDestinationFloor, expectedStartingAndDestinationFloor);

        int actualStartingFloor = human.getStartingFloor();
        int actualDestinationFloor = human.getDestinationFloor();

        assertEquals(expectedStartingAndDestinationFloor, actualStartingFloor,
                "The human needs to support that both, starting and destination floor, have the same value.");

        assertEquals(expectedStartingAndDestinationFloor, actualDestinationFloor,
                "The human needs to support that both, starting and destination floor, have the same value.");
    }

    @Test
    void testCurrentEnteredElevatorId() {
        int anyStartingFloor = 1;
        int anyDestinationFloor = 3;
        Human human = new Human(anyStartingFloor, anyDestinationFloor);

        OptionalInt currentEnteredElevatorId = human.getCurrentEnteredElevatorId();

        assertTrue(currentEnteredElevatorId.isEmpty(),
                "The getCurrentEnteredElevatorId() method of Human needs to return the current entered elevator ID, which is supposed to be empty for now.");
    }

    @Test
    void testImplementsElevatorListener() {
        assertTrue(ElevatorListener.class.isAssignableFrom(Human.class),
                "The Human class is supposed to implement the ElevatorListener interface.");
    }
}
