package org.togetherjava.event.elevator.elevators;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.togetherjava.event.elevator.elevators.ElevatorSystem.mod;

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
    private int numberOfHumansAboard;
    private int humanExited;
    private List<Integer> requestedDestinationFloors;
    private TravelDirection travellingDirection;

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
        this.requestedDestinationFloors = new ArrayList<>();
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

    public TravelDirection getTravellingDirection() {
        return travellingDirection;
    }

    @Override
    public void requestDestinationFloor(int destinationFloor) {
        // TODO Implement. This represents a human or the elevator system
        //  itself requesting this elevator to eventually move to the given floor.
        //  The elevator is supposed to memorize the destination in a way that
        //  it can ensure to eventually reach it.
        requestedDestinationFloors.add(destinationFloor);
        if(destinationFloor > currentFloor) travellingDirection = TravelDirection.UP;
        else if(destinationFloor < currentFloor) travellingDirection = TravelDirection.DOWN;
        else travellingDirection = null;
        System.out.println("Request for destination floor received");
    }

    public void moveOneFloor() {
        // TODO Implement. Essentially there are three possibilities:
        //  - move up one floor
        //  - move down one floor
        //  - stand still
        //  The elevator is supposed to move in a way that it will eventually reach
        //  the floors requested by Humans via requestDestinationFloor(), ideally "fast" but also "fair",
        //  meaning that the average time waiting (either in corridor or inside the elevator)
        //  is minimized across all humans.
        //  It is essential that this method updates the currentFloor field accordingly.
        if(requestedDestinationFloors.isEmpty()) {
            lastResort();
            return;
        }
        sortingList();
        int currentRequest = requestedDestinationFloors.getLast();
        if(currentRequest > currentFloor) {
            currentFloor++;
            travellingDirection = TravelDirection.UP;
        }
        else if(currentRequest < currentFloor) {
            currentFloor--;
            travellingDirection = TravelDirection.DOWN;
        }
        else {
            requestedDestinationFloors.removeLast();
            removeRequests();
        }
        System.out.println("Request to move a floor received");
    }

    /**
     * It is possible that the requests ends for the current elevator but there are still humans onboard.
     * In that case the paternoster is the last resort to make sure the humans onboard reach the destination floor.
     */
    private void lastResort() {
        if(currentFloor == minFloor) travellingDirection = TravelDirection.UP;
        else if(currentFloor == (minFloor + floorsServed -1)) travellingDirection = TravelDirection.DOWN;
        else if(numberOfHumansAboard != 0) {
            if(travellingDirection == TravelDirection.UP) currentFloor++;
            else if(travellingDirection == TravelDirection.DOWN) currentFloor--;
        }
    }

    /**
     * For instance, a number of humans can exit to the current floor.
     * But the request for the current floor can make the elevator come to the current floor again or stay at it for consecutive steps.
     * To prevent such loss of steps, this method removes the number of request according to the number of humans exited.
     */
    private void removeRequests() {
        for(int i = 0; i < humanExited; i++) {
            for(int j = 0; j < requestedDestinationFloors.size(); j++) {
                if(requestedDestinationFloors.get(j) == currentFloor) {
                    requestedDestinationFloors.remove(j);
                    humanExited--;
                    j--;
                }
            }
        }
    }

    /**
     * To sort the list based on how far the requested floor is from the current floor
     * The idea is to make the elevator go to the nearest or farthest request based on strategy.
     */

    private void sortingList() {
        for(int i = 0; i < requestedDestinationFloors.size() - 1; i++) {
            if(mod(requestedDestinationFloors.get(i) - currentFloor) < mod(requestedDestinationFloors.get(i+1)) - currentFloor) {
                int temp = requestedDestinationFloors.get(i + 1);
                requestedDestinationFloors.remove(i + 1);
                requestedDestinationFloors.add(i + 1,requestedDestinationFloors.get(i));
                requestedDestinationFloors.remove(i);
                requestedDestinationFloors.add(i,temp);
            }
        }
    }

    @Override
    public void enteredElevator() {
        numberOfHumansAboard++;
    }

    @Override
    public void exitedElevator() {
        numberOfHumansAboard--;
        humanExited++;
    }

    @Override
    public synchronized String toString() {
        return new StringJoiner(", ", Elevator.class.getSimpleName() + "[", "]").add("id=" + id)
                .add("minFloor=" + minFloor)
                .add("floorsServed=" + floorsServed)
                .add("currentFloor=" + this.currentFloor)
                .toString();
    }
}
