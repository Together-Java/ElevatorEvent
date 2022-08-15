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
    }

    public State getCurrentState() {
        return currentState;
    }

    public int getStartingFloor() {
        return startingFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    private TravelDirection getTravelDirection()
    {
        if(destinationFloor>=startingFloor)
            return TravelDirection.UP;
        else
            return TravelDirection.DOWN;
    }
    @Override
    public void onElevatorSystemReady(FloorPanelSystem floorPanelSystem) {
        // TODO Implement. The system is now ready and the human should leave
        //  their initial IDLE state, requesting an elevator by clicking on the buttons of
        //  the floor panel system. The human will now enter the WAITING_FOR_ELEVATOR state.
        if(startingFloor==destinationFloor)
        {
            currentEnteredElevatorId=null;
            currentState=State.ARRIVED;
            return;
        }
        floorPanelSystem.requestElevator(startingFloor,getTravelDirection());
        currentState=State.WAITING_FOR_ELEVATOR;
    }

    @Override
    public void onElevatorArrivedAtFloor(ElevatorPanel elevatorPanel) {
        // TODO Implement. If the human is currently waiting for an elevator and
        //  this event represents arrival at the humans current floor, the human can now enter the
        //  elevator and request their actual destination floor. The state has to change to TRAVELING_WITH_ELEVATOR.
        //  If the human is currently traveling with this elevator and the event represents
        //  arrival at the human's destination floor, the human can now exit the elevator.
        if(currentState==State.WAITING_FOR_ELEVATOR && elevatorPanel.getCurrentFloor()==startingFloor)
        {
            elevatorPanel.requestDestinationFloor(destinationFloor);
            currentState=State.TRAVELING_WITH_ELEVATOR;
            currentEnteredElevatorId=elevatorPanel.getId();
        }
        else if(currentState==State.TRAVELING_WITH_ELEVATOR && elevatorPanel.getId()==currentEnteredElevatorId && elevatorPanel.getCurrentFloor()==destinationFloor)
        {
            currentEnteredElevatorId=null;
            currentState=State.ARRIVED;
        }

    }

    public OptionalInt getCurrentEnteredElevatorId() {
        return currentEnteredElevatorId == null
                ? OptionalInt.empty()
                : OptionalInt.of(currentEnteredElevatorId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Human.class.getSimpleName() + "[", "]")
                .add("currentState=" + currentState)
                .add("startingFloor=" + startingFloor)
                .add("destinationFloor=" + destinationFloor)
                .add("currentEnteredElevatorId=" + currentEnteredElevatorId)
                .toString();
    }

    public enum State {
        IDLE,
        WAITING_FOR_ELEVATOR,
        TRAVELING_WITH_ELEVATOR,
        ARRIVED
    }
}
