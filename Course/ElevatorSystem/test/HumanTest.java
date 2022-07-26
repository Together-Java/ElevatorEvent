import org.junit.jupiter.api.Test;
import org.togetherjava.event.elevator.humans.ElevatorListener;
import org.togetherjava.event.elevator.humans.Human;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class HumanTest {
    @Test
    void testImplementsElevatorListener() {
        assertTrue(ElevatorListener.class.isAssignableFrom(Human.class),
                "The Human class is supposed to implement the ElevatorListener interface.");
    }
}
