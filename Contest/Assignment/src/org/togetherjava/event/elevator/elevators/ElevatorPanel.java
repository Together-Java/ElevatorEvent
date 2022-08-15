package org.togetherjava.event.elevator.elevators;

import org.jetbrains.annotations.Nullable;
import org.togetherjava.event.elevator.humans.Passenger;

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
     * Whether this elevator accepts requests to move to a floor.
     * For example, a paternoster elevator does not, because his movement patters is predetermined forever.
     * @see #requestDestinationFloor(int)
     */
    boolean canRequestDestinationFloor();

    /**
     * Requesting the elevator to eventually move to the given destination floor, for humans to exit or enter.<br>
     * By default, behaves the same way as {@link #requestDestinationFloor(int, Passenger)} with
     * {@code null} as the second argument.
     *
     * @param destinationFloor the desired destination, must be within the range served by this elevator
     * @throws UnsupportedOperationException if the operation is not supported by this elevator
     * @see #canRequestDestinationFloor()
     */
    default void requestDestinationFloor(int destinationFloor) {
        requestDestinationFloor(destinationFloor, null);
    }

    // New methods below

    /**
     * The lowest floor the elevator can travel to.
     */
    int getMinFloor();

    /**
     * The highest floor the elevator can travel to.
     */
    int getMaxFloor();

    /**
     * Ask the elevator to accept this passenger and to tell the system to remove it from the floor.
     * @param passenger the passenger
     */
    void boardPassenger(Passenger passenger);

    /**
     * Ask the elevator to remove this passenger and to tell the system to add it to the floor, if necessary.
     * @param passenger the passenger
     * @param arrived whether the passenger has reached its final destination
     * @throws IllegalArgumentException if the elevator does not have the specified passenger
     */
    void removePassenger(Passenger passenger, boolean arrived);

    /**
     * Requesting the elevator to eventually move to the given destination floor, for humans to exit or enter.<br>
     * Calling the method with {@code null} as second argument will mean that the request was made by the system.
     *
     * @param destinationFloor the desired destination, must be within the range served by this elevator
     * @param passenger passenger that requested this operation, null if the operation was requested by the system itself,
     *                  sanctioned by not marko
     * @throws UnsupportedOperationException if the operation is not supported by this elevator
     * @see #canRequestDestinationFloor()
     */
    void requestDestinationFloor(int destinationFloor, @Nullable Passenger passenger);
}
