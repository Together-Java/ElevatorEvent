package org.togetherjava.event.elevator.humans;

import org.togetherjava.event.elevator.elevators.ElevatorPanel;
import org.togetherjava.event.elevator.elevators.FloorPanelSystem;
import org.togetherjava.event.elevator.elevators.TravelDirection;

import java.util.OptionalInt;
import java.util.StringJoiner;

/**
 * A single human that starts at a given floor and wants to
 * reach a destination floor via an elevator.
 * <p>
 * The class mainly acts upon given elevator events it listens to,
 * for example requesting an elevator, eventually entering and exiting them.
 */
public final class Human implements ElevatorListener {
    private State currentState;
    private final int startingFloor;
    private final int destinationFloor;
    private final TravelDirection desiredDirection;
    private final Object lock = new Object();
    /**
     * If the human is currently inside an elevator, this is its unique ID.
     * Otherwise, this is {@code null} to indicate that the human is currently on the corridor.
     */
    private Integer currentEnteredElevatorId;

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
        this.startingFloor = startingFloor;
        this.destinationFloor = destinationFloor;
        currentState = State.IDLE;
        if (startingFloor == destinationFloor) {
            this.desiredDirection = TravelDirection.NONE;
        } else {
            this.desiredDirection = (destinationFloor > startingFloor) ? TravelDirection.UP : TravelDirection.DOWN;
        }
    }

    public State getCurrentState() {
        synchronized (lock) {
            return currentState;
        }
    }

    public int getStartingFloor() {
       return startingFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    @Override
    public void onElevatorSystemReady(FloorPanelSystem floorPanelSystem) {
        synchronized (lock) {
            currentState = State.WAITING_FOR_ELEVATOR;
            floorPanelSystem.requestElevator(startingFloor, desiredDirection);
        }
    }

    @Override
    public void onElevatorArrivedAtFloor(ElevatorPanel elevatorPanel) {
        synchronized (lock) {
            if (currentState == State.WAITING_FOR_ELEVATOR && desiredDirection == TravelDirection.NONE) {
                currentEnteredElevatorId = null;
                currentState = State.ARRIVED;
                return;
            }
            if (currentState.equals(State.WAITING_FOR_ELEVATOR)
                    && elevatorPanel.getCurrentFloor() == startingFloor
                /* or advertised direction equals TravelDirection.NONE part is needed here as advertised direction
                can be none if this was the last stop of the existing queue but more people need to board still */
                    && ((elevatorPanel.getAdvertisedDirection() == desiredDirection)
                        || elevatorPanel.getAdvertisedDirection() == TravelDirection.NONE)) {
                currentEnteredElevatorId = elevatorPanel.getId();
                elevatorPanel.requestDestinationFloor(destinationFloor);
                currentState = State.TRAVELING_WITH_ELEVATOR;
            }
            if (currentState.equals(State.TRAVELING_WITH_ELEVATOR)
                    && elevatorPanel.getId() == currentEnteredElevatorId
                    && elevatorPanel.getCurrentFloor() == destinationFloor) {
                currentEnteredElevatorId = null;
                currentState = State.ARRIVED;
            }
        }
    }

    public OptionalInt getCurrentEnteredElevatorId() {
        synchronized (lock) {
            return currentEnteredElevatorId == null
                    ? OptionalInt.empty()
                    : OptionalInt.of(currentEnteredElevatorId);
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            return new StringJoiner(", ", Human.class.getSimpleName() + "[", "]")
                    .add("currentState=" + currentState)
                    .add("startingFloor=" + startingFloor)
                    .add("destinationFloor=" + destinationFloor)
                    .add("currentEnteredElevatorId=" + currentEnteredElevatorId)
                    .toString();
        }
    }

    public enum State {
        IDLE,
        WAITING_FOR_ELEVATOR,
        TRAVELING_WITH_ELEVATOR,
        ARRIVED
    }
}
