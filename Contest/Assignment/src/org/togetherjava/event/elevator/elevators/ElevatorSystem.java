package org.togetherjava.event.elevator.elevators;

import org.togetherjava.event.elevator.humans.ElevatorListener;

import java.util.*;

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

            final Elevator bestElevator = getBestElevator(atFloor, desiredTravelDirection);
            bestElevator.requestDestinationFloor(atFloor);

        System.out.println("Request for elevator received");
    }

    /**
     * Cases covered :-
     * 1. Lift has no requests, that means travelling direction is null. This makes sure each and every elevator moves to
     * take requests from humans.
     * 2. Lift has requests, and it'll go in the direction of the said human currently requesting.
     * This prioritises that the human requesting hops in the elevator moving in their direction.
     * 3.Lift has requests, and it'll go against the direction of the said human currently requesting.
     * This ensures that humans will get a lift coming in there direction even if they may have to wait longer.
     *
     * @param atFloor the floor from which the human is requesting from.
     * @param desiredTravelDirection the direction said human wants to go, this will help to provide them the best lift.
     * @return
     */
    private Elevator getBestElevator(int atFloor, TravelDirection desiredTravelDirection) {
        Elevator bestElevator = elevators.getFirst();
        for(Elevator elevator : elevators) {
            if(elevator.getTravellingDirection() == null && mod((bestElevator.getCurrentFloor() - atFloor)) > mod((elevator.getCurrentFloor() - atFloor))) {
                bestElevator = elevator;
            }
            else if(elevator.getTravellingDirection() == desiredTravelDirection && mod((bestElevator.getCurrentFloor() - atFloor)) > mod((elevator.getCurrentFloor() - atFloor))) {
                bestElevator = elevator;
            }
            else if(mod((bestElevator.getCurrentFloor() - atFloor)) > mod((elevator.getCurrentFloor() - atFloor))) {
                bestElevator = elevator;
            }
        }
        return bestElevator;
    }

    static int mod(int num) {
        int modValue = num;
        if(modValue < 0) modValue *= -1;
        return modValue;
    }

    public void moveOneFloor() {
        elevators.forEach(Elevator::moveOneFloor);
        elevators.forEach(elevator -> elevatorListeners.forEach(listener -> listener.onElevatorArrivedAtFloor(elevator)));
    }
}
