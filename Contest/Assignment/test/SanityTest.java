import org.junit.jupiter.api.Test;
import org.togetherjava.event.elevator.elevators.Elevator;
import org.togetherjava.event.elevator.humans.Human;
import org.togetherjava.event.elevator.simulation.Simulation;

import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

final class SanityTest {
    private static final String ERROR_MESSAGE_PRE =
            "Sanity checks for 'Simulation.createRandomSimulation(5, 5, 50, 10)' failed. ";

    @Test
    void testSimulationSanity() {
        Simulation simulation = Simulation.createRandomSimulation(5, 5, 50, 10);
        int stepLimit = 1_000;

        requiredHumansToBeIdle(simulation);

        var previousSnapshot = new SimulationSnapshot(simulation);

        simulation.start();

        var currentSnapshot = new SimulationSnapshot(simulation);
        verifySnapshotSanity(previousSnapshot, currentSnapshot, simulation);
        previousSnapshot = currentSnapshot;

        requiredHumansNotToBeIdle(simulation);

        while (!simulation.isDone()) {
            simulation.step();

            currentSnapshot = new SimulationSnapshot(simulation);
            verifySnapshotSanity(previousSnapshot, currentSnapshot, simulation);
            previousSnapshot = currentSnapshot;

            if (simulation.getStepCount() >= stepLimit) {
                fail(ERROR_MESSAGE_PRE + "All humans should have arrived by now, but they did not."
                        + " There is likely a bug in your code.");
            }
        }
    }

    private static void requiredHumansToBeIdle(Simulation simulation) {
        for (Human human : simulation.getHumans()) {
            assertEquals(Human.State.IDLE, human.getCurrentState(), ERROR_MESSAGE_PRE
                    + "Before the start of the simulation, all humans should be in the IDLE state. But '%s' was not.".formatted(
                    human));
        }
    }

    private static void requiredHumansNotToBeIdle(Simulation simulation) {
        for (Human human : simulation.getHumans()) {
            assertNotEquals(Human.State.IDLE, human.getCurrentState(), ERROR_MESSAGE_PRE
                    + "Right after the start of the simulation, all humans should not be in the IDLE state anymore. But '%s' was.".formatted(
                    human));
        }
    }

    private static void verifySnapshotSanity(SimulationSnapshot previousSnapshot,
            SimulationSnapshot currentSnapshot, Simulation simulation) {
        for (Human human : simulation.getHumans()) {
            HumanSnapshot previousHumanSnapshot = previousSnapshot.getHumanSnapshot(human);
            HumanSnapshot currentHumanSnapshot = currentSnapshot.getHumanSnapshot(human);

            Human.State previousState = previousHumanSnapshot.state();
            Human.State currentState = currentHumanSnapshot.state();

            boolean changedBackToIdle =
                    previousState != Human.State.IDLE && currentState == Human.State.IDLE;
            assertFalse(changedBackToIdle, ERROR_MESSAGE_PRE
                    + "Humans must never change their state back to IDLE. But '%s' did.".formatted(
                    human));

            boolean changedOutOfArrived =
                    previousState == Human.State.ARRIVED && currentState != Human.State.ARRIVED;
            assertFalse(changedOutOfArrived, ERROR_MESSAGE_PRE
                    + "Once a human arrived, they must never change their state again. But '%s' did.".formatted(
                    human));

            boolean enteredElevator = previousState == Human.State.WAITING_FOR_ELEVATOR
                    && currentState == Human.State.TRAVELING_WITH_ELEVATOR;
            if (enteredElevator) {
                OptionalInt maybeElevatorId = currentHumanSnapshot.currentElevatorId();
                assertTrue(maybeElevatorId.isPresent(), ERROR_MESSAGE_PRE
                        + "When a human enters an elevator, they need a current elevator id. But '%s' does not.".formatted(
                        human));
                assertTrue(previousHumanSnapshot.currentElevatorId().isEmpty(), ERROR_MESSAGE_PRE
                        + "When a human enters an elevator, they must not have been in an elevator previously. But '%s' was.".formatted(
                        human));

                ElevatorSnapshot currentElevatorSnapshot =
                        currentSnapshot.getElevatorSnapshot(maybeElevatorId.orElseThrow());
                assertEquals(human.getStartingFloor(), currentElevatorSnapshot.currentFloor(),
                        ERROR_MESSAGE_PRE
                                + "When a human enters an elevator, the elevator must be at the humans starting floor. But '%s' entered elevator with ID '%d' at a different floor.".formatted(
                                human, maybeElevatorId.orElseThrow()));
            }

            boolean exitedElevator =
                    previousState != Human.State.ARRIVED && currentState == Human.State.ARRIVED;
            if (exitedElevator) {
                assertTrue(currentHumanSnapshot.currentElevatorId().isEmpty(), ERROR_MESSAGE_PRE
                        + "When a human exits an elevator, they must not have a current elevator id anymore. But '%s' has.".formatted(
                        human));

                // Only if the human actually travelled around
                if (human.getStartingFloor() != human.getDestinationFloor()) {
                    OptionalInt maybeElevatorId = previousHumanSnapshot.currentElevatorId();
                    assertTrue(maybeElevatorId.isPresent(), ERROR_MESSAGE_PRE
                            + "When a human exits an elevator, they must have had a current elevator id previously. But '%s' does not.".formatted(
                            human));

                    assertEquals(Human.State.TRAVELING_WITH_ELEVATOR, previousState,
                            "When a human exits an elevator, their previous state must be traveling. But '%s' was in state %s.".formatted(
                                    human, previousState));

                    ElevatorSnapshot currentElevatorSnapshot =
                            currentSnapshot.getElevatorSnapshot(maybeElevatorId.orElseThrow());
                    assertEquals(human.getDestinationFloor(),
                            currentElevatorSnapshot.currentFloor(), ERROR_MESSAGE_PRE
                                    + "When a human exits an elevator, the elevator must be at the humans destination floor. But '%s' exited elevator with ID '%d' at a different floor.".formatted(
                                    human, maybeElevatorId.orElseThrow()));
                }
            }

            for (Elevator elevator : simulation.getElevators()) {
                int previousFloor =
                        previousSnapshot.getElevatorSnapshot(elevator.getId()).currentFloor();
                int currentFloor =
                        currentSnapshot.getElevatorSnapshot(elevator.getId()).currentFloor();

                int travelDistance = Math.abs(previousFloor - currentFloor);
                assertTrue(travelDistance <= 1, ERROR_MESSAGE_PRE
                        + "Elevators must either travel 0 or 1 floor each step, but '%s' travelled %d floors.".formatted(
                        elevator, travelDistance));

                int minFloor = elevator.getMinFloor();
                int maxFloor = minFloor + elevator.getFloorsServed() - 1;

                assertTrue(currentFloor >= minFloor, ERROR_MESSAGE_PRE
                        + "Elevators must never travel beyond their min floor, but '%s' reached floor '%d'.".formatted(
                        elevator, currentFloor));
                assertTrue(currentFloor <= maxFloor, ERROR_MESSAGE_PRE
                        + "Elevators must never travel beyond their max floor, but '%s' reached floor '%d'.".formatted(
                        elevator, currentFloor));
            }
        }
    }

    private record HumanSnapshot(Human.State state, OptionalInt currentElevatorId) {
        static HumanSnapshot of(Human human) {
            return new HumanSnapshot(human.getCurrentState(), human.getCurrentEnteredElevatorId());
        }
    }


    private record ElevatorSnapshot(int currentFloor) {
        static ElevatorSnapshot of(Elevator elevator) {
            return new ElevatorSnapshot(elevator.getCurrentFloor());
        }
    }


    private static class SimulationSnapshot {
        private final Map<Human, HumanSnapshot> humanToSnapshots;
        private final Map<Integer, ElevatorSnapshot> elevatorIdToSnapshots;

        SimulationSnapshot(Simulation simulation) {
            humanToSnapshots = simulation.getHumans()
                    .stream()
                    .collect(Collectors.toMap(Function.identity(), HumanSnapshot::of));

            elevatorIdToSnapshots = simulation.getElevators()
                    .stream()
                    .collect(Collectors.toMap(Elevator::getId, ElevatorSnapshot::of));
        }

        HumanSnapshot getHumanSnapshot(Human human) {
            return humanToSnapshots.get(human);
        }

        ElevatorSnapshot getElevatorSnapshot(int elevatorId) {
            return elevatorIdToSnapshots.get(elevatorId);
        }
    }
}
