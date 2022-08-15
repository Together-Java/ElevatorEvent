package org.togetherjava.event.elevator.elevators;

import org.togetherjava.event.elevator.humans.ElevatorListener;
import org.togetherjava.event.elevator.humans.Passenger;
import org.togetherjava.event.elevator.util.ConcurrentUtils;
import org.togetherjava.event.elevator.util.LogUtils;

import java.util.*;

/**
 * System controlling all elevators of a building.
 * <p>
 * Once all elevators and humans have been registered via {@link #registerElevator(Elevator)}
 * and {@link #registerElevatorListener(ElevatorListener)} respectively,
 * the system can be made ready using {@link #ready()}.
 */
public final class ElevatorSystem implements FloorPanelSystem {
    private final Collection<Elevator> elevators = new HashSet<>();
    private final NavigableMap<Integer, Floor> floors = new TreeMap<>();

    public void registerElevator(Elevator elevator) {
        elevators.add(elevator);
        elevator.setElevatorSystem(this);

        for (int i = elevator.getMinFloor(); i <= elevator.getMaxFloor(); i++) {
            floors.computeIfAbsent(i, Floor::new);
        }

        floors.get(elevator.getCurrentFloor()).addElevator(elevator);
    }

    public void registerElevatorListener(ElevatorListener listener) {
        if (listener instanceof Passenger passenger && passenger.wantsToMove()) {
            floors.get(passenger.getCurrentFloor()).addPassenger(passenger);
        }
    }

    /**
     * Upon calling this, the system is ready to receive elevator requests. Elevators may now start moving.<br>
     * <br>
     * Additionally, elevator arrival events are fired so that humans can immediately enter them.
     */
    public void ready() {
        LogUtils.measure("Elevator requests", () -> ConcurrentUtils.performTasksInParallel(floors.values(), f -> f.fireElevatorRequestEvents(this)));
        LogUtils.measure("Elevator arrivals", () -> ConcurrentUtils.performTasksInParallel(floors.values(), Floor::fireElevatorArrivalEvents));
    }

    void passengerEnteredElevator(Passenger passenger) {
        floors.get(passenger.getCurrentFloor()).removePassenger(passenger);
    }

    void passengerLeftElevator(Passenger passenger, boolean arrived) {
        if (!arrived) {
            floors.get(passenger.getCurrentFloor()).addPassenger(passenger);
        }
    }

    /**
     * This represents a human standing in the corridor,
     * requesting that an elevator comes to pick them up for travel into the given direction.
     * The system is supposed to make sure that an elevator will eventually reach this floor to pick up the human.
     * The human can then enter the elevator and request their actual destination within the elevator.
     * Ideally this has to select the best elevator among all which can reduce the time
     * for the human spending waiting (either in corridor or in the elevator itself).
     *
     * @param atFloor                the floor to pick up the human at, must be within the range served by the system
     * @param desiredTravelDirection the direction the human wants to travel into,
     *                               can be used for determination of the best elevator
     * @param listener               (NEW) the listener that requested the operation, only used to slightly
     *                               improve the elevator selection algorithm, think an array of surveillance cameras
     *                               and fingerprint sensors in buttons (scary to think about though), sanctioned by
     *                               not marko
     * @return the id of the elevator that was recommended by the system
     */
    @Override
    public int requestElevator(int atFloor, TravelDirection desiredTravelDirection, ElevatorListener listener) {
        Elevator elevator;

        int target = calculateAverageTarget(atFloor, desiredTravelDirection)
                .orElseThrow(() -> new IllegalArgumentException("Impossible to travel %s from floor %d".formatted(desiredTravelDirection.name(), atFloor)));

        synchronized (elevators) {
            if (elevators.isEmpty()) {
                throw new IllegalStateException("An elevator was requested, but there are none registered in the system");
            }

            elevator = elevators.stream()
                    .filter(e -> e.canServe(atFloor, atFloor + (desiredTravelDirection == TravelDirection.UP ? 1 : -1)))
                    .min((e1, e2) -> {
                        // Calculate the time it would take for both elevators to reach the request floor and the target
                        int t1 = e1.turnsToVisit(atFloor, target);
                        int t2 = e2.turnsToVisit(atFloor, target);
                        // If they both can actually reach it, just compare the numbers
                        if (t1 >= 0 && t2 >= 0) {
                            return Integer.compare(t1, t2);
                        }
                        // If one of them cannot reach it, prefer the one that can
                        else if (t1 >= 0) {
                            return -1;
                        } else if (t2 >= 0) {
                            return 1;
                        }
                        // At this point, the target lies outside the range of both elevators
                        // In this case, choose the elevator which boundaries lie closest to the target
                        return Integer.compare(
                                Math.min(Math.abs(e1.getMaxFloor() - target), Math.abs(e1.getMinFloor() - target)),
                                Math.min(Math.abs(e2.getMaxFloor() - target), Math.abs(e2.getMinFloor() - target))
                        );
                    })
                    .orElseThrow(() -> new IllegalStateException("No elevators can go %s from floor %d".formatted(desiredTravelDirection.name(), atFloor)));

        }

        if (elevator.canRequestDestinationFloor()) {
            elevator.requestDestinationFloor(atFloor);
            elevator.addPotentialTarget(target, listener);
        }

        return elevator.getId();
    }

    public int getFloorAmount() {
        return floors.size();
    }

    public int getMinFloor() {
        return floors.firstEntry().getKey();
    }

    public int getMaxFloor() {
        return floors.lastEntry().getKey();
    }

    /**
     * A helper method to determine whether the simulation is still running.
     * Created to avoid streaming the entire Human registry.
     */
    public boolean hasActivePassengers() {
        return floors.values().stream()
                .map(Floor::getActivePassengersCount)
                .reduce(0, Integer::sum) > 0;
    }

    public void moveOneFloor() {
        LogUtils.measure("Moving elevators", this::moveElevators);
        LogUtils.measure("Listener firing", this::fireFloorListeners);
    }

    /**
     * Estimate average target floor that a user might select given a starting floor and a direction.
     * Picks the middle floor between the starting floor and the one farthest in the given direction.
     * @return {@link OptionalInt} describing the calculated floor number, or empty if the request doesn't make sense
     * (going up from the topmost floor or down from the bottom floor)
     */
    private OptionalInt calculateAverageTarget(int floorFrom, TravelDirection desiredTravelDirection) {
        if (desiredTravelDirection == TravelDirection.UP) {
            int maxFloor = floors.lastEntry().getKey();
            if (floorFrom >= maxFloor) {
                return OptionalInt.empty();
            } else {
                int delta = maxFloor - floorFrom;
                delta = delta % 2 == 0 ? delta / 2 : delta / 2 + 1;
                return OptionalInt.of(floorFrom + delta);
            }
        } else {
            int minFloor = floors.firstEntry().getKey();
            if (floorFrom <= minFloor) {
                return OptionalInt.empty();
            } else {
                int delta = floorFrom - minFloor;
                delta = delta % 2 == 0 ? delta / 2 : delta / 2 + 1;
                return OptionalInt.of(floorFrom - delta);
            }
        }
    }

    private void moveElevators() {
        ConcurrentUtils.performTasksInParallel(elevators, e -> {
            floors.get(e.getCurrentFloor()).removeElevator(e);
            e.moveOneFloor();
            floors.get(e.getCurrentFloor()).addElevator(e);
        });
    }

    /**
     * Elevator passengers are notified first, giving them a chance to exit and potentially remove themselves
     * from tracking if they have reached their destination. In the majority of cases, this will be a performance
     * improvement compared to firing waiting passenger events first.
     * Lastly, once everyone who wanted to board did so, notify any remaining idle passengers that they can request
     * an elevator. This helps to introduce new passengers to the system.
     */
    private void fireFloorListeners() {
        ConcurrentUtils.performTasksInParallel(floors.values(), f -> {
            f.fireElevatorPassengerEvents();
            f.fireElevatorArrivalEvents();
            f.fireElevatorRequestEvents(this);
        });
    }
}
