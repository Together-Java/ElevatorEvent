package org.togetherjava.event.elevator.elevators;

import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A single elevator that can serve a given amount of floors.
 * <p>
 * An elevator can take floor requests from either humans or the elevator system itself.
 * The elevator will eventually move towards the requested floor and transport humans to their destinations.
 */
public final class Elevator implements ElevatorPanel {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
    private static final String INVALID_FLOOR_ERROR = "Floor is outside the valid range for this elevator!";

    private final int id;
    private final int minFloor;
    private final int floorsServed;
    private final int maxFloor;
    private final int[] requests;
    private final Object lock = new Object();
    private int currentFloor;
    private TravelDirection currentDirection;

    /**
     * Creates a new elevator.
     *
     * @param minFloor     the minimum floor that the elevator can serve, must be greater than or equal to 1.
     * @param floorsServed the amount of floors served in total by this elevator, must be greater than or equal to 2.
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
        this.maxFloor = minFloor + floorsServed - 1;
        this.currentDirection = TravelDirection.NONE;
        requests = new int[floorsServed];
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
        synchronized (lock) {
            return currentFloor;
        }
    }

    @Override
    public void requestDestinationFloor(int destinationFloor) {
        synchronized (lock) {
            addRequest(destinationFloor);
        }
    }

    @Override
    public TravelDirection getAdvertisedDirection() {
        synchronized (lock) {
            if (!moreRequestsInCurrentDirection()) {
                return computeBestDirection();
            }
            return currentDirection;
        }
    }

    /**
     * Moves the elevator one floor
     * Elevator can either: move up one floor, move down one floor, or stand still
     */
    public void moveOneFloor() {
        synchronized (lock) {
            clearRequests(currentFloor);
            if (!moreRequestsInCurrentDirection()) {
                currentDirection = computeBestDirection();
            }
            if (currentDirection == TravelDirection.UP) {
                goUp();
            } else if (currentDirection == TravelDirection.DOWN) {
                goDown();
            }
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            return new StringJoiner(", ", Elevator.class.getSimpleName() + "[", "]").add("id=" + id)
                    .add("minFloor=" + minFloor)
                    .add("floorsServed=" + floorsServed)
                    .add("currentFloor=" + currentFloor)
                    .toString();
        }
    }

    /**
     * Determines if there are additional requests in currentDirection in requests array for this elevator
     *
     * @return a boolean (true means there are more requests in current direction, otherwise false)
     */
    private boolean moreRequestsInCurrentDirection() {
        synchronized (lock) {
            if (currentDirection == TravelDirection.NONE) {
                return getRequestCount(currentFloor) > 0;
            }
            if (currentDirection == TravelDirection.UP) {
                return countRequestsAbove() > 0;
            }
            if (currentDirection == TravelDirection.DOWN) {
                return countRequestsBelow() > 0;
            }
            throw new RuntimeException("currentDirection is in an invalid state!");
        }
    }

    /**
     * Determines the best direction to travel based on request count in each direction
     *
     * @return the desired TravelDirection to move in
     */
    private TravelDirection computeBestDirection() {
        synchronized (lock) {
            int requestsAbove = countRequestsAbove();
            int requestsBelow = countRequestsBelow();
            if (requestsAbove == 0 && requestsBelow == 0) {
                return TravelDirection.NONE;
            }
            return (requestsAbove >= requestsBelow) ? TravelDirection.UP : TravelDirection.DOWN;
        }
    }

    /**
     * Counts the requests above the currentFloor for the current elevator instance
     *
     * @return an integer count of requests above the current floor
     */
    private int countRequestsAbove() {
        synchronized (lock) {
            int requests = 0;
            for (int i = currentFloor + 1; i <= maxFloor; i++) {
                requests += getRequestCount(i);
            }
            return requests;
        }
    }

    /**
     * Counts the requests below the currentFloor for the current elevator instance
     *
     * @return an integer count of requests below the current floor
     */
    private int countRequestsBelow() {
        synchronized (lock) {
            int requests = 0;
            for (int i = currentFloor - 1; i >= minFloor; i--) {
                requests += getRequestCount(i);
            }
            return requests;
        }
    }

    /**
     * Moves the elevator up one floor
     */
    private void goUp() {
        synchronized (lock) {
            currentFloor += 1;
        }
    }

    /**
     * Moves the elevator down one floor
     */
    private void goDown() {
        synchronized (lock) {
            currentFloor -= 1;
        }
    }

    /**
     * Adds a request to the current elevator for the desired floor. Should be used alongside
     * {@link Elevator#getRequestCount(int)} to retrieve the counts to ensure offsets remain consistent.
     *
     * @param floor the floor the request is for
     * @throws IllegalArgumentException if floor is outside valid range
     */
    private void addRequest(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException(INVALID_FLOOR_ERROR);
        }
        synchronized (lock) {
            requests[floor - minFloor] += 1;
        }
    }

    /**
     * Gets the number of requests from the current elevator for a given floor
     *
     * @param floor the floor to get the request count from
     * @return an integer with the request count
     * @throws IllegalArgumentException if floor is outside valid range
     */
    private int getRequestCount(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException(INVALID_FLOOR_ERROR);
        }
        synchronized (lock) {
            return requests[floor - minFloor];
        }
    }

    /**
     * Removes requests for a given floor
     *
     * @param floor the floor to clear requests from
     * @throws IllegalArgumentException if floor is outside valid range
     */
    private void clearRequests(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException(INVALID_FLOOR_ERROR);
        }
        synchronized (lock) {
            requests[floor - minFloor] = 0;
        }
    }
}
