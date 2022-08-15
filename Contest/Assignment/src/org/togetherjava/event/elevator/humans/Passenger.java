package org.togetherjava.event.elevator.humans;

/**
 * A passenger that wants to travel from one floor to another.
 */
public interface Passenger extends ElevatorListener {
    /**
     * Where is the passenger travelling from.
     */
    int getStartingFloor();

    /**
     * Where the passenger wants to travel to.
     */
    int getDestinationFloor();

    /**
     * Passenger's current floor.
     */
    int getCurrentFloor();

    /**
     * Whether this passenger currently wants to move to a different floor.
     */
    default boolean wantsToMove() {
        return getCurrentFloor() != getDestinationFloor();
    }
}
