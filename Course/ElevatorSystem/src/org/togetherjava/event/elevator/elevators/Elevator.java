package org.togetherjava.event.elevator.elevators;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A single elevator that can serve a given amount of floors.
 * <p>
 * An elevator can take floor requests from either humans or the elevator system itself.
 * The elevator will eventually move towards the requested floor and transport humans to their destinations.
 */
public final class Elevator implements ElevatorPanel {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    private final int id;
    private final int minFloor;
    private final int floorsServed;
    private int currentFloor;

    /**
     * Creates a new elevator.
     *
     * @param minFloor     the minimum floor that the elevator can serve, must be greater equals 1
     * @param floorsServed the amount of floors served in total by this elevator, must be greater equals 2.
     *                     Together with the minFloor this forms a consecutive range of floors with no gaps in between.
     * @param currentFloor the floor the elevator starts at, must be within the defined range of floors served by the elevator
     */
    public Elevator(int minFloor, int floorsServed, int currentFloor) {
        if (minFloor <= 0 || floorsServed < 2) {
            throw new IllegalArgumentException("Min floor must at least 1, floors served at least 2.");
        }
        if (currentFloor < minFloor || currentFloor >= minFloor + floorsServed) {
            throw new IllegalArgumentException("The current floor must be between the floors served by the elevator.");
        }

        this.id = NEXT_ID.getAndIncrement();
        this.minFloor = minFloor;
        this.currentFloor = currentFloor;
        this.floorsServed = floorsServed;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public int getFloorsServed() {
        return floorsServed;
    }

    @Override
    public int getCurrentFloor() {
        return currentFloor;
    }

    @Override
    public void requestDestinationFloor(int destinationFloor) {
        System.out.println("Request for destination floor received");
    }

    public void moveOneFloor() {
        System.out.println("Request to move a floor received");
    }

    // NOTE Put any extra code here, then it carries over to the next task
}
