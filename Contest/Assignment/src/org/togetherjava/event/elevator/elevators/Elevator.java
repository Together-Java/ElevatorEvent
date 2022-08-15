package org.togetherjava.event.elevator.elevators;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.togetherjava.event.elevator.humans.ElevatorListener;
import org.togetherjava.event.elevator.humans.Passenger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Common superclass for all elevators.
 * <p>
 * An elevator may be able to take floor requests from either humans or the elevator system itself. In that case,
 * the elevator will eventually move towards the requested floor and transport humans to their destinations.
 */
public abstract class Elevator implements ElevatorPanel {
    protected static final Logger logger = LogManager.getLogger();
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    @Getter protected final int id;
    @Getter protected final int minFloor;
    @Getter protected final int maxFloor;

    /**
     * Currently boarded passengers.
     */
    @Getter protected final Collection<Passenger> passengers = ConcurrentHashMap.newKeySet();

    /**
     * Target queue which holds floors that this elevator must visit.
     */
    protected final Deque<Integer> targets = new ArrayDeque<>();

    /**
     * A map which holds guesses of next potential targets, populated by the {@link ElevatorSystem}.
     * This is the whole reason to make the system aware of passengers who make elevator calls.
     * It's not necessary, but is believed to ever so slightly improve the elevator selection algorithm
     * over just a simple set/list of potential targets without owners.
     */
    protected final Map<ElevatorListener, Integer> potentialTargets = new LinkedHashMap<>();
    @Getter protected int currentFloor;
    /**
     * An elevator should be aware of the system it belongs to.
     */
    protected ElevatorSystem elevatorSystem;

    /**
     * Creates a new elevator.
     *
     * @param minFloor     the minimum floor that the elevator can serve, must be greater than or equal to 1.
     * @param floorsServed the amount of floors served in total by this elevator, must be greater than or equal to 2.
     *                     Together with the minFloor this forms a consecutive range of floors with no gaps in between.
     * @param currentFloor the floor the elevator starts at, must be within the defined range of floors served by the elevator
     */
    public Elevator(int minFloor, int floorsServed, int currentFloor) {
        if (minFloor < 1) {
            throw new IllegalArgumentException("Minimum floor must at least 1, got " + minFloor);
        }
        if (floorsServed < 2) {
            throw new IllegalArgumentException("Amount of served floors must be at least 2, got " + floorsServed);
        }
        int maxFloor = minFloor + floorsServed - 1;
        if (currentFloor < minFloor || maxFloor < currentFloor) {
            throw new IllegalArgumentException("The current floor for this elevator must be between %d and %d, got %d".formatted(minFloor, maxFloor, currentFloor));
        }

        this.id = NEXT_ID.getAndIncrement();
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.currentFloor = currentFloor;
    }

    public int getFloorsServed() {
        return maxFloor - minFloor + 1;
    }

    public int getTaskCount() {
        return targets.size();
    }

    void setElevatorSystem(ElevatorSystem elevatorSystem) {
        if (elevatorSystem == null) {
            throw new IllegalArgumentException("Elevator system must not be null");
        }
        this.elevatorSystem = elevatorSystem;
    }

    public void boardPassenger(Passenger passenger) {
        if (elevatorSystem == null) {
            throw new IllegalStateException("Elevator is not connected to an elevator system");
        }
        if (passengers.contains(passenger)) {
            throw new IllegalArgumentException("Attempt to add a passenger which is already in the elevator");
        }
        passengers.add(passenger);
        elevatorSystem.passengerEnteredElevator(passenger);
    }

    public void removePassenger(Passenger passenger, boolean arrived) {
        if (elevatorSystem == null) {
            throw new IllegalStateException("Elevator is not connected to an elevator system");
        }
        if (!passengers.contains(passenger)) {
            throw new IllegalArgumentException("Attempt to remove a passenger which is not in the elevator");
        }
        passengers.remove(passenger);
        elevatorSystem.passengerLeftElevator(passenger, arrived);
    }

    /**
     * Whether this elevator accepts requests to move to a floor.
     * For example, a paternoster elevator does not, because his movement pattern is predetermined forever.
     * @see #requestDestinationFloor(int, Passenger)
     */
    public abstract boolean canRequestDestinationFloor();

    /**
     * This represents a human or the elevator system
     * itself requesting this elevator to eventually move to the given floor.
     * The elevator is supposed to memorize the destination in a way that
     * it can ensure to eventually reach it.
     *
     * @throws UnsupportedOperationException if the operation is not supported by this elevator
     * @see #canRequestDestinationFloor()
     */
    @Override
    public abstract void requestDestinationFloor(int destinationFloor, @Nullable Passenger passenger);

    /**
     * Add a potential future target for this elevator. Improves the elevator selection algorithm.
     */
    synchronized void addPotentialTarget(int potentialTarget, ElevatorListener listener) {
        if (!potentialTargets.containsKey(listener) && !potentialTargets.containsValue(potentialTarget)) {
            potentialTargets.put(listener, clampFloor(potentialTarget));
            logger.debug(() -> "Elevator %d on floor %d has added potential target %d, the queue is now %s, potential targets %s".formatted(id, currentFloor, potentialTarget, targets, potentialTargets.values()));
        }
    }

    /**
     * Essentially there are three possibilities:
     * <ul>
     *  <li>move up one floor</li>
     *  <li>move down one floor</li>
     *  <li>stand still</li>
     *  </ul>
     *  The elevator is supposed to move in a way that it will eventually reach
     *  the floors requested by Humans via {@link #requestDestinationFloor(int, Passenger)}, ideally "fast" but also "fair",
     *  meaning that the average time waiting (either in corridor or inside the elevator)
     *  is minimized across all humans.
     *  It is essential that this method updates the currentFloor field accordingly.
     */
    public void moveOneFloor() {
        if (!targets.isEmpty()) {
            int target = targets.element();
            if (currentFloor < target) {
                currentFloor++;
            } else if (currentFloor > target) {
                currentFloor--;
            } else {
                throw new IllegalArgumentException("Elevator has current floor as next target, this is a bug");
            }
            if (currentFloor == target) {
                // We arrived at the next target
                modifyTargetsOnArrival();
            }
        } else if (!potentialTargets.isEmpty()) {
            logger.debug(() -> "Elevator %d on floor %d is idling and will clear its potential targets".formatted(id, currentFloor));
            potentialTargets.clear();
        }
    }

    /**
     * The action to perform once the elevator reaches a target.
     */
    protected abstract void modifyTargetsOnArrival();

    @Override
    public synchronized String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]").add("id=" + id)
                .add("minFloor=" + minFloor)
                .add("maxFloor=" + maxFloor)
                .add("currentFloor=" + currentFloor)
                .add("passengers=" + passengers.size())
                .toString();
    }

    @Override
    public int hashCode() {
        return id;
    }

    /**
     * @return whether this elevator can serve all the specified floors.
     */
    public boolean canServe(int... floors) {
        for (int floor : floors) {
            if (floor < minFloor || floor > maxFloor) {
                return false;
            }
        }
        return true;
    }

    /**
     * @throws IllegalArgumentException if the specified floor cannot be served by this elevator.
     */
    protected void rangeCheck(int floor) {
        if (!canServe(floor)) {
            throw new IllegalArgumentException("Elevator cannot serve floor %d, only %d to %d are available".formatted(floor, minFloor, maxFloor));
        }
    }

    /**
     * Returns a floor value clamped between max and min floors.
     */
    protected int clampFloor(int floor) {
        return Math.max(Math.min(floor, maxFloor), minFloor);
    }

    /**
     * @return whether this elevator is currently on the specified floor
     * or will at some point visit that floor before all its tasks are done.
     */
    public abstract boolean willVisitFloor(int floor);

    /**
     * @return the minimum amount of turns it would take for this elevator to visit a specified sequence of floors
     * (either indirectly by passing by or by creating new tasks),
     * taking into account all potential targets of this elevator,
     * or -1 if it's impossible or of the input array is empty
     * @implNote a choice was made to use {@code int} over {@link OptionalInt OptionalInt}
     * since the amount of turns cannot be negative
     */
    public synchronized int turnsToVisit(int... floors) {
        if (floors.length == 0) {
            return -1;
        }

        int count = 0;
        int previousTarget = currentFloor;

        Collection<Integer> allTargets = new ArrayList<>(targets.size() + potentialTargets.size());
        allTargets.addAll(targets);
        allTargets.addAll(potentialTargets.values());
        Iterator<Integer> targetItr = allTargets.iterator();
        Iterator<Integer> floorItr = Arrays.stream(floors).iterator();

        int nextFloor = floorItr.next();
        if (!canServe(nextFloor)) {
            return -1;
        }

        while (targetItr.hasNext()) {
            int nextTarget = targetItr.next();

            // While the next floor we're interested in lies on the path,
            // we "chop off" part of the path, adding the length of that part to count
            // we also advance the floor iterator, or return if the floor was last
            while (previousTarget <= nextFloor && nextFloor <= nextTarget || previousTarget >= nextFloor && nextFloor >= nextTarget) {
                count += Math.abs(nextFloor - previousTarget);
                previousTarget = nextFloor;
                if (floorItr.hasNext()) {
                    nextFloor = floorItr.next();
                    if (!canServe(nextFloor)) {
                        return -1;
                    }
                } else {
                    return count;
                }
            }
            // If there are more floors remaining to check, add what's left of currently inspected path
            count += Math.abs(nextTarget - previousTarget);

            previousTarget = nextTarget;
        }

        // The floor currently at nextFloor is guaranteed to be unprocessed
        count += Math.abs(nextFloor - previousTarget);
        previousTarget = nextFloor;
        // If after traversing the queue we haven't covered all floors that we wanted,
        // simulate adding them to the queue
        while (floorItr.hasNext()) {
            nextFloor = floorItr.next();
            if (!canServe(nextFloor)) {
                return -1;
            }

            count += Math.abs(nextFloor - previousTarget);
            previousTarget = nextFloor;
        }

        return count;
    }
}
