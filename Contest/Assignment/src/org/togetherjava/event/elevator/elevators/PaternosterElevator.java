package org.togetherjava.event.elevator.elevators;

import org.jetbrains.annotations.Nullable;
import org.togetherjava.event.elevator.humans.Passenger;

/**
 * A Paternoster lift which endlessly travels in a range between two floors. It cannot take requests.
 */
public final class PaternosterElevator extends Elevator {
    /**
     * Creates a new Paternoster elevator.
     *
     * @param minFloor     the minimum floor that the elevator can serve, must be greater than or equal to 1.
     * @param floorsServed the amount of floors served in total by this elevator, must be greater than or equal to 2.
     *                     Together with the minFloor this forms a consecutive range of floors with no gaps in between.
     * @param currentFloor the floor the elevator starts at, must be within the defined range of floors served by the elevator
     */
    public PaternosterElevator(int minFloor, int floorsServed, int currentFloor) {
        this(minFloor, floorsServed,currentFloor, TravelDirection.UP);
    }

    /**
     * Creates a new Paternoster elevator.
     *
     * @param minFloor     the minimum floor that the elevator can serve, must be greater than or equal to 1.
     * @param floorsServed the amount of floors served in total by this elevator, must be greater than or equal to 2.
     *                     Together with the minFloor this forms a consecutive range of floors with no gaps in between.
     * @param currentFloor the floor the elevator starts at, must be within the defined range of floors served by the elevator
     * @param startingDirection desired starting direction, will be overridden if the elevator is currently
     *                          at the end of the path and cannot move further in that direction
     */
    public PaternosterElevator(int minFloor, int floorsServed, int currentFloor, TravelDirection startingDirection) {
        super(minFloor, floorsServed, currentFloor);
        if (this.currentFloor == this.minFloor || this.currentFloor != this.maxFloor && startingDirection == TravelDirection.UP) {
            targets.add(this.maxFloor);
            targets.add(this.minFloor);
        } else {
            targets.add(this.minFloor);
            targets.add(this.maxFloor);
        }
    }

    /**
     * Cannot request floors since this elevator has predetermined movement
     */
    @Override
    public boolean canRequestDestinationFloor() {
        return false;
    }

    @Override
    public synchronized void requestDestinationFloor(int destinationFloor, @Nullable Passenger passenger) {
        throw new UnsupportedOperationException("Paternoster elevator does not accept requests");
    }

    /**
     * Reinsert the target at the back of the queue
     */
    @Override
    protected void modifyTargetsOnArrival() {
        targets.add(targets.remove());
    }

    /**
     * Since this is a Paternoster elevator, it will always be able to visit floor in his range,
     * and will never visit other floors.
     *
     * @return whether this elevator is currently on the specified floor
     * or will at some point visit that floor before all its tasks are done.
     */
    @Override
    public boolean willVisitFloor(int floor) {
        return canServe(floor);
    }
}
