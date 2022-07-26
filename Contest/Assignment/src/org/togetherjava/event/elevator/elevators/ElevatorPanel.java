package org.togetherjava.event.elevator.elevators;

/**
 * The system inside an elevator which provides information about the elevator and can be
 * used to request a destination floor.
 */
public interface ElevatorPanel {
    /**
     * The unique ID of the elevator.
     *
     * @return the unique ID of the elevator
     */
    int getId();

    /**
     * The floor the elevator is currently at.
     *
     * @return the current floor
     */
    int getCurrentFloor();

    /**
     * Requesting the elevator to eventually move to the given destination floor, for humans to exit.
     *
     * @param destinationFloor the desired destination, must be within the range served by this elevator
     */
    void requestDestinationFloor(int destinationFloor);
}
