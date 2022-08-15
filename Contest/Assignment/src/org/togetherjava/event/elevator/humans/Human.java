package org.togetherjava.event.elevator.humans;

import lombok.Getter;
import org.togetherjava.event.elevator.elevators.ElevatorPanel;
import org.togetherjava.event.elevator.elevators.FloorPanelSystem;
import org.togetherjava.event.elevator.elevators.TravelDirection;

import java.util.OptionalInt;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A single human that starts at a given floor and wants to
 * reach a destination floor via an elevator.
 * <p>
 * The class mainly acts upon given elevator events it listens to,
 * for example requesting an elevator, eventually entering and exiting them.
 */
public final class Human implements Passenger {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    @Getter private final int id;
    @Getter private final int startingFloor;
    @Getter private final int destinationFloor;
    @Getter private State currentState;
    @Getter private int currentFloor;
    private int nextDestination;
    private int expectedElevatorId = -1;
    /**
     * If the human is currently inside an elevator, this the reference to it.
     * Otherwise, this is {@code null} to indicate that the human is currently on the corridor.
     */
    private ElevatorPanel currentEnteredElevator;

    /**
     * Creates a new human.
     * <p>
     * It is supported that starting and destination floors are equal.
     * The human will then not travel with an elevator at all.
     *
     * @param startingFloor    the floor the human currently stands at, must be greater than or equal to 1
     * @param destinationFloor the floor the human eventually wants to reach, must be greater than or equal to 1
     */
    public Human(int startingFloor, int destinationFloor) {
        if (startingFloor <= 0 || destinationFloor <= 0) {
            throw new IllegalArgumentException("Floors must be at least 1");
        }

        this.id = NEXT_ID.getAndIncrement();
        this.startingFloor = startingFloor;
        this.currentFloor = startingFloor;
        this.destinationFloor = destinationFloor;

        if (startingFloor == destinationFloor) {
            currentState = State.ARRIVED;
        } else {
            currentState = State.IDLE;
        }
    }

    /**
     * The system is now ready and the human should leave
     * their initial IDLE state, requesting an elevator by clicking on the buttons of
     * the floor panel system. The human will now enter the WAITING_FOR_ELEVATOR state.
     */
    @Override
    public synchronized void onElevatorSystemReady(FloorPanelSystem floorPanelSystem) {
        if (currentState == State.IDLE) {
            TravelDirection direction;
            if (currentFloor < destinationFloor) {
                direction = TravelDirection.UP;
            } else if (currentFloor > destinationFloor) {
                direction = TravelDirection.DOWN;
            } else {
                // Do nothing. Why did this human come to the elevator hall? :thinking:
                System.out.printf("A human has matching source and destination floors, it will be counted as arrived: %d and %d%n", startingFloor, destinationFloor);
                currentState = State.ARRIVED;
                return;
            }
            expectedElevatorId = floorPanelSystem.requestElevator(currentFloor, direction, this);
            currentState = State.WAITING_FOR_ELEVATOR;
        }
    }

    /**
     * If the human is currently waiting for an elevator and
     * this event represents arrival at the humans current floor, the human can now enter the
     * elevator and request their actual destination floor. The state has to change to TRAVELING_WITH_ELEVATOR.
     * If the human is currently traveling with this elevator and the event represents
     * arrival at the human's destination floor, the human can now exit the elevator.
     */
    @Override
    public synchronized void onElevatorArrivedAtFloor(ElevatorPanel elevatorPanel) {
        if (shouldBoardElevator(elevatorPanel)) {
            if (currentFloor < destinationFloor) {

                int elevatorMaxFloor = elevatorPanel.getMaxFloor();
                if (elevatorMaxFloor <= currentFloor) {
                    // This human wants to go up, but the elevator cannot go up, skip
                    return;
                }

                elevatorPanel.boardPassenger(this);
                nextDestination = Math.min(elevatorMaxFloor, destinationFloor);

                if (elevatorPanel.canRequestDestinationFloor()) {
                    elevatorPanel.requestDestinationFloor(nextDestination, this);
                }
            } else if (currentFloor > destinationFloor) {

                int elevatorMinFloor = elevatorPanel.getMinFloor();
                if (elevatorMinFloor >= currentFloor) {
                    // This human wants to go down, but the elevator cannot go down, skip
                    return;
                }

                elevatorPanel.boardPassenger(this);
                nextDestination = Math.max(elevatorMinFloor, destinationFloor);

                if (elevatorPanel.canRequestDestinationFloor()) {
                    elevatorPanel.requestDestinationFloor(nextDestination, this);
                }
            } else {
                throw new RuntimeException("A human's current floor matches destination floor, but they are waiting for an elevator, this is a bug");
            }

            currentEnteredElevator = elevatorPanel;
            currentState = State.TRAVELING_WITH_ELEVATOR;
        } else if (isInElevator(elevatorPanel)) {
            currentFloor = elevatorPanel.getCurrentFloor();

            if (currentFloor == nextDestination) {
                boolean arrived = nextDestination == destinationFloor;
                elevatorPanel.removePassenger(this, arrived);
                currentEnteredElevator = null;
                expectedElevatorId = -1;
                if (arrived) {
                    currentState = State.ARRIVED;
                } else {
                    currentState = State.IDLE;
                }
            }
        }
    }

    private boolean shouldBoardElevator(ElevatorPanel elevatorPanel) {
        return currentState == State.WAITING_FOR_ELEVATOR && elevatorPanel.getId() == expectedElevatorId;
    }

    private boolean isInElevator(ElevatorPanel elevatorPanel) {
        return currentState == State.TRAVELING_WITH_ELEVATOR && elevatorPanel.equals(currentEnteredElevator);
    }

    public OptionalInt getCurrentEnteredElevatorId() {
        return currentEnteredElevator == null
                ? OptionalInt.empty()
                : OptionalInt.of(currentEnteredElevator.getId());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Human.class.getSimpleName() + "[", "]")
                .add("currentState=" + currentState)
                .add("startingFloor=" + startingFloor)
                .add("currentFloor=" + currentFloor)
                .add("destinationFloor=" + destinationFloor)
                .add("currentEnteredElevatorId=" + (currentEnteredElevator == null ? "null" : currentEnteredElevator.getId()))
                .toString();
    }

    @Override
    public int hashCode() {
        return id;
    }

    public enum State {
        IDLE,
        WAITING_FOR_ELEVATOR,
        TRAVELING_WITH_ELEVATOR,
        ARRIVED
    }
}
