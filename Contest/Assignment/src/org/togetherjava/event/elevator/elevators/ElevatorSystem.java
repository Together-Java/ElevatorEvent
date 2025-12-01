package org.togetherjava.event.elevator.elevators;

import org.togetherjava.event.elevator.humans.ElevatorListener;
import java.util.ArrayList;
import java.util.List;

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
        elevatorListeners.forEach(listener -> listener.onElevatorSystemReady(this));
    }

    @Override
    public void requestElevator(int atFloor, TravelDirection desiredTravelDirection) {
        findNearestInDesiredDirection(elevators, atFloor, desiredTravelDirection).requestDestinationFloor(atFloor);
    }

    public void moveOneFloor() {
        elevators.parallelStream().forEach(Elevator::moveOneFloor);
        elevators.parallelStream().forEach(elevator -> elevatorListeners.parallelStream().forEach(listener -> listener.onElevatorArrivedAtFloor(elevator)));
    }

    /**
     * Helper method that finds the nearest elevator to a given floor in position to travel in the desired direction.
     *
     * @param elevators the list of elevators
     * @param floor the desired floor (where the human resides)
     * @param desiredDirection the desired TravelDirection
     * @return elevator object representing the nearest in that direction OR a fallback to the nearest in general if
     * none fit the direction criteria
     */
    private static Elevator findNearestInDesiredDirection(List<Elevator> elevators, int floor, TravelDirection desiredDirection) {
        List<Elevator> directionMatchedElevators = new ArrayList<>();
        for (Elevator e : elevators) {
            if (desiredDirection == TravelDirection.DOWN) {
                if (e.getCurrentFloor() > floor) {
                    directionMatchedElevators.add(e);
                }
            } else if (desiredDirection == TravelDirection.UP) {
                if (e.getCurrentFloor() < floor){
                    directionMatchedElevators.add(e);
                }
            }
        }
        Elevator result;
        if (!directionMatchedElevators.isEmpty()) {
            result = findNearestElevator(directionMatchedElevators, floor);
        } else {
            result = findNearestElevator(elevators, floor);
        }
        if (result == null) {
            throw new RuntimeException("Found no elevators < Integer.MAX_VALUE distance away from complete list of elevators!");
        }
        return result;
    }

    /**
     * A helper method that returns the nearest elevator to the given floor.
     * NOTE: Returned elevator can be {@code null} (be careful if list can be empty)
     *
     * @param elevators the list of elevators
     * @param floor the floor you want to find the nearest elevator to
     * @return the closest Elevator or null if none found
     */
    private static Elevator findNearestElevator(List<Elevator> elevators, int floor) {
        Elevator nearest = null;
        int nearestDistance = Integer.MAX_VALUE;
        for (Elevator elevator : elevators) {
            int distance = Math.abs(elevator.getCurrentFloor() - floor);
            if (distance < nearestDistance) {
                nearest = elevator;
                nearestDistance = distance;
            }
        }
        return nearest;
    }
}