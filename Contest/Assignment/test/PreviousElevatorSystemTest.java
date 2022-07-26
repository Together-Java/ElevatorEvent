import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.togetherjava.event.elevator.elevators.Elevator;
import org.togetherjava.event.elevator.elevators.ElevatorSystem;
import org.togetherjava.event.elevator.elevators.FloorPanelSystem;
import org.togetherjava.event.elevator.humans.ElevatorListener;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

final class PreviousElevatorSystemTest {
    @Test
    void testImplementsFloorPanelSystem() {
        assertTrue(FloorPanelSystem.class.isAssignableFrom(ElevatorSystem.class),
                "The ElevatorSystem class is supposed to implement the FloorPanelSystem interface.");
    }

    @Test
    void testReady() {
        ElevatorSystem system = new ElevatorSystem();

        List<ElevatorListener> listeners =
                Stream.generate(() -> mock(ElevatorListener.class)).limit(5).toList();

        listeners.forEach(system::registerElevatorListener);

        system.ready();

        for (ElevatorListener listener : listeners) {
            verify(listener, description(
                    "The 'ready' method of ElevatorSystem is supposed to invoke 'onElevatorSystemReady' on all registered elevator listeners.")).onElevatorSystemReady(
                    system);
        }
    }

    @Test
    void testMoveOneFloor() {
        ElevatorSystem system = new ElevatorSystem();

        Supplier<Elevator> createAnyElevator = () -> new Elevator(1, 5, 2);
        List<Elevator> elevators = Stream.generate(createAnyElevator).limit(3).toList();

        List<ElevatorListener> listeners =
                Stream.generate(() -> mock(ElevatorListener.class)).limit(5).toList();

        elevators.forEach(system::registerElevator);
        listeners.forEach(system::registerElevatorListener);
        system.ready();

        system.moveOneFloor();
        for (Elevator elevator : elevators) {
            for (ElevatorListener listener : listeners) {
                verify(listener, description(
                        "The 'moveOneFloor' method of ElevatorSystem is supposed to invoke 'onElevatorArrivedAtFloor' on all registered elevator listeners for each registered elevator.")).onElevatorArrivedAtFloor(
                        elevator);
            }
        }
    }
}
