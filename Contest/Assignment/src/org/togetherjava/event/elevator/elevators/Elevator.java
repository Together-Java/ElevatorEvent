package org.togetherjava.event.elevator.elevators;

import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

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
    private  List<Integer> destinations;

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
        this.destinations = new ArrayList<>();
    }

    /**
     * The unique ID of the elevator.
     *
     * @return the unique ID of the elevator
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * The lowest floor the elevator can service.
     *
     * @return the lowest floor the elevator can service.
     */
    public int getMinFloor() {
        return minFloor;
    }

    /**
     * The amount of floors the elevator can service.
     *
     * @return the amount of floors the elevator can service.
     */
    public int getFloorsServed() {
        return floorsServed;
    }

    /**
     * The floor the elevator is currently at.
     *
     * @return the floor the elevator is currently at.
     */
    @Override
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * The chosen Elevator will store this destination in it,s arrayList so it knows
     * which direction to move in and when it has arrived.
     * It will not store the same destination twice.
     *
     * @param destinationFloor stores the destination of the humans entering it
     */
    @Override
    public void requestDestinationFloor(int destinationFloor) {

        if(!destinations.contains(destinationFloor)){
            destinations.add(destinationFloor);
        }

        System.out.println("Request for destination floor received");
    }

    // create a method that will store the destinations in order:
    // closest to the current floor. So every time an elevator moved one floor,
    // we have to call this method as wel, because humans entering add to the
    // array.

    /**
     * Moving the elevator one floor in the correct direction and
     * when it arrived at it,s destination it will remove that floor from
     * it,s arrayList: destinations.
     */
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

        // try to let an empty elevator help an elevator with more humans to transport.
        //System.out.println("Request to move a floor received on elevator id: " + getId() + "--- " + getCurrentFloor());
        System.out.println(destinations);
        if(destinations.isEmpty()){
            return;
        }

        int destination = destinations.get(0);

        if (this.currentFloor < destination) {
            this.currentFloor++;
        } else if (this.currentFloor > destination) {
            this.currentFloor--;
        } else {
            System.out.println("Elevator " + id + " ARRIVED at floor " + currentFloor);
            destinations.remove(0);
        }
    }

    /**
     * Prints the data of the fields of class Elevator
     *
     * @return the data of the fields of class Elevator.
     **/
    @Override
    public synchronized String toString() {
        return new StringJoiner(", ", Elevator.class.getSimpleName() + "[", "]").add("id=" + id)
                .add("minFloor=" + minFloor)
                .add("floorsServed=" + floorsServed)
                .add("currentFloor=" + currentFloor)
                .toString();
    }
}
