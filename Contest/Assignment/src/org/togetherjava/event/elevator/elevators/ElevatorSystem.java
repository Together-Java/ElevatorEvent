package org.togetherjava.event.elevator.elevators;

import org.togetherjava.event.elevator.humans.ElevatorListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

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
        // TODO Implement. This represents a human standing in the corridor,
        //  requesting that an elevator comes to pick them up for travel into the given direction.
        //  The system is supposed to make sure that an elevator will eventually reach this floor to pick up the human.
        //  The human can then enter the elevator and request their actual destination within the elevator.
        //  Ideally this has to select the best elevator among all which can reduce the time
        //  for the human spending waiting (either in corridor or in the elevator itself).
        //System.out.println("Request for elevator received");
        Elevator elevatorToUse = elevators
                .stream()
                .sorted(getComparator(atFloor))
                .filter(getElevatorFilter(desiredTravelDirection))
                .findFirst()
                .orElse(elevators
                        .stream()
                        .min(getComparator(atFloor))
                        .orElseThrow(() -> new IllegalStateException("There are no Elevators")));

        elevatorToUse.setTravelDirection(desiredTravelDirection);

        elevatorToUse.humansWaiting.add(atFloor);
    }

    private Predicate<Elevator> getElevatorFilter(TravelDirection desiredTravelDirection) {
        return (elevator1) -> elevator1.getTravelDirection().isEmpty() || elevator1.getTravelDirection().get() == desiredTravelDirection;
    }

    private Comparator<Elevator> getComparator(int atFloor) {
        return Comparator.comparingInt((ele) -> Math.abs(atFloor - ele.getCurrentFloor()));
    }

    public void moveOneFloor() {
        elevators.forEach(Elevator::moveOneFloor);
        elevatorListeners.forEach((human) -> elevators.forEach(human::onElevatorArrivedAtFloor));
    }
}
