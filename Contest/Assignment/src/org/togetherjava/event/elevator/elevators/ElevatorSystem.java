package org.togetherjava.event.elevator.elevators;

import org.togetherjava.event.elevator.humans.ElevatorListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * System controlling all elevators of a building.
 * <p>
 * Once all elevators and humans have been registered via {@link #registerElevator(Elevator)}
 * and {@link #registerElevatorListener(ElevatorListener)} respectively,
 * the system can be made ready using {@link #ready()}.
 */
public final class ElevatorSystem implements FloorPanelSystem {
    private final List<Elevator> elevators = new ArrayList<>();
    private final List<ElevatorListener> elevatorListeners = new ArrayList<>();

    public void registerElevator(Elevator elevator) {
        elevators.add(elevator);
    }

    public void registerElevatorListener(ElevatorListener listener) {
        elevatorListeners.add(listener);
    }

    /**
     * Upon calling this, the system is ready to receive elevator requests. Elevators may now start moving.
     */
    public void ready() {
        elevatorListeners.parallelStream().forEach(listener -> listener.onElevatorSystemReady(this));
    }

    public static int floorAndElevatorDistance(int floor, Elevator elevator) {
        return Math.abs(elevator.getCurrentFloor()-floor);
    }
    @Override
    public Elevator bestElevator(int atFloor, TravelDirection desiredTravelDirection) {
        assert !elevators.isEmpty();
        Elevator bestElevator = elevators.getFirst();
        if (elevators.size() == 1) {
            return bestElevator;
        }
        Set<Elevator> candidates = new HashSet<>(); //we find all the closest candidates
        candidates.add(bestElevator);
        for (var elevator : elevators) {
            if (floorAndElevatorDistance(atFloor, elevator) < floorAndElevatorDistance(atFloor, candidates.stream().findAny().get())) {
                candidates.clear();
                candidates.add(elevator);
            } else if (floorAndElevatorDistance(atFloor, elevator) == floorAndElevatorDistance(atFloor, candidates.stream().findAny().get())) {
                candidates.add(elevator);
            }
        }
        bestElevator = candidates.stream().findAny().get();
        for (var elevator : candidates) {
            if (elevator.getFloorRequests().size() < bestElevator.getFloorRequests().size()) { //out of the closest candidates, we chose the one with the least traffic
                bestElevator = elevator;
            }
        }
        return bestElevator;
    }

    public void requestElevator(Elevator elevator, int atFloor) {
        elevator.requestDestinationFloor(atFloor);
    }

    @Override
    public void requestElevator(int atFloor, TravelDirection desiredTravelDirection) {
        // TODO Implement. This represents a human standing in the corridor,
        //  requesting that an elevator comes to pick them up for travel into the given direction.
        //  The system is supposed to make sure that an elevator will eventually reach this floor to pick up the human.
        //  The human can then enter the elevator and request their actual destination within the elevator.
        //  Ideally this has to select the best elevator among all which can reduce the time
        //  for the human spending waiting (either in corridor or in the elevator itself).
        bestElevator(atFloor, desiredTravelDirection).requestDestinationFloor(atFloor);
    }

    public void moveOneFloor() {
        elevators.parallelStream().forEach(Elevator::moveOneFloor);
        elevators.parallelStream().forEach(elevator -> elevatorListeners.parallelStream().forEach(listener -> listener.onElevatorArrivedAtFloor(elevator)));
    }
}
