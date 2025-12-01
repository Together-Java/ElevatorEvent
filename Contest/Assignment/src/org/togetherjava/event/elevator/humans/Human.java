package org.togetherjava.event.elevator.humans;

import org.togetherjava.event.elevator.elevators.*;

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
        this.currentEnteredElevatorId = null;
    }

    /**
     * The state the human is currently in.
     *
     * @return the State the human is in
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * The floor the human currently stands at.
     *
     * @return the floor the human currently stands at
     */
    public int getStartingFloor() {
        return startingFloor;
    }


    /**
     * The foor the human wants to reach.
     *
     * @return the floor the human wants to reach.
     */
    public int getDestinationFloor() {
        return destinationFloor;
    }


    /**
     * This method will set the human State to WAITING and set the travelDirection
     * Then it will call the requestElevator system.
     * @param floorPanelSystem the system in the corridor that allows
     *                         requesting elevators to the current floor
     */
    @Override
    public void onElevatorSystemReady(FloorPanelSystem floorPanelSystem) {
        // TODO Implement. The system is now ready and the human should leave
        //  their initial IDLE state, requesting an elevator by clicking on the buttons of
        //  the floor panel system. The human will now enter the WAITING_FOR_ELEVATOR state.
        // System.out.println("Ready-event received");

        currentState = State.WAITING_FOR_ELEVATOR;
        TravelDirection chosenDirection = null;

        if(this.startingFloor < this.destinationFloor) {
            chosenDirection = TravelDirection.UP;
        } else if(this.startingFloor > this.destinationFloor){
            chosenDirection = TravelDirection.DOWN;
        }

        floorPanelSystem.requestElevator(getStartingFloor(), chosenDirection);
    }

    /**
     * his method will set the human State to TRAVELING.
     * It will give the human the ID of the elevator it entered.
     * Then it will call the requestDestination method, setting the destination.
     * If the elevator arrived at the destination it will set the State to ARRIVED
     * and the ID to null.
     * @param elevatorPanel the system inside the elevator which provides information
     *                      about the elevator and can be used to request a destination floor.
     */
    @Override
    public void onElevatorArrivedAtFloor(ElevatorPanel elevatorPanel) {
        // TODO Implement. If the human is currently waiting for an elevator and
        //  this event represents arrival at the humans current floor, the human can now enter the
        //  elevator and request their actual destination floor. The state has to change to TRAVELING_WITH_ELEVATOR.
        //  If the human is currently traveling with this elevator and the event represents
        //  arrival at the human's destination floor, the human can now exit the elevator.
        //System.out.println("Arrived-event received");

        int elevatorId = elevatorPanel.getId();
        int elevatorFloor = elevatorPanel.getCurrentFloor();
        if (this.currentState == State.WAITING_FOR_ELEVATOR && this.startingFloor == elevatorFloor) {
            this.currentState = State.TRAVELING_WITH_ELEVATOR;
            this.currentEnteredElevatorId = elevatorId;
            elevatorPanel.requestDestinationFloor(this.destinationFloor);
            return;
        }
        if (this.currentState == State.TRAVELING_WITH_ELEVATOR && elevatorFloor == this.destinationFloor
                && this.currentEnteredElevatorId == elevatorId) {
                    this.currentState = State.ARRIVED;
                    this.currentEnteredElevatorId = null;
        }
    }

    public OptionalInt getCurrentEnteredElevatorId() {
        return currentEnteredElevatorId == null
                ? OptionalInt.empty()
                : OptionalInt.of(currentEnteredElevatorId);
    }

    /**
     * returns the data of the Human class fields.
     * @return the data of the Human class fields.
     */
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
