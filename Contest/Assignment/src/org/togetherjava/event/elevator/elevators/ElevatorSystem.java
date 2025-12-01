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

    /**
     * Registers all Elevator fields in an ArrayList
     * @param elevator a single elevator with it,s fields data, created in the Elevator class.
     */
    public void registerElevator(Elevator elevator) {
        elevators.add(elevator);
    }

    /**
     * Registers all ElevatorListeners objects (humans) in an ArrayList
     * @param listener a single human with it,s fields data, created in the Human class.
     */
    public void registerElevatorListener(ElevatorListener listener) {
        elevatorListeners.add(listener);
    }

    /**
     * Upon calling this, the system is ready to receive elevator requests. Elevators may now start moving.
     * Every human in the arrayList listeners calls this method.
     * {@link org.togetherjava.event.elevator.humans.Human#onElevatorSystemReady(FloorPanelSystem)}  }
     */
    public void ready() {
        // elevatorListeners.forEach(listener -> listener.onElevatorSystemReady(this));

        for (ElevatorListener listener : elevatorListeners) {
            listener.onElevatorSystemReady(this);
        }
    }

    @Override
    public void requestElevator(int atFloor, TravelDirection desiredTravelDirection) {
        // TODO Implement. This represents a human standing in the corridor,
        //  requesting that an elevator comes to pick them up for travel into the given direction.
        //  The system is supposed to make sure that an elevator will eventually reach this floor to pick up the human.
        //  The human can then enter the elevator and request their actual destination within the elevator.
        //  Ideally this has to select the best elevator among all which can reduce the time
        //  for the human spending waiting (either in corridor or in the elevator itself).

        if (elevators.isEmpty()){
            return;
        }
        int calculateDistance = 0;

        Elevator chosenElevator = this.elevators.get(0);
        if (chosenElevator.getCurrentFloor() < atFloor){
            calculateDistance = atFloor - chosenElevator.getCurrentFloor();
        } else if (chosenElevator.getCurrentFloor() > atFloor){
            calculateDistance = chosenElevator.getCurrentFloor() - atFloor;
        }

        for (Elevator elevator : elevators) {

            int distance = 0;
            if (elevator.getCurrentFloor() < atFloor){
                distance = atFloor - elevator.getCurrentFloor();
            } else if (elevator.getCurrentFloor() > atFloor){
                distance = elevator.getCurrentFloor() - atFloor;
            }

            if (calculateDistance > distance) {
                calculateDistance = distance;
                chosenElevator = elevator;
            }
        }
        //System.out.println("calculate distance = " + calculateDistance);

        // elevator panel is now being used:
        chosenElevator.requestDestinationFloor(atFloor);
        System.out.println("Request for elevator received at Floor: " + atFloor);
        System.out.println("chosen Elevator = " + chosenElevator);
    }


    /**
     * First it runs the {@link org.togetherjava.event.elevator.elevators.Elevator#moveOneFloor()} method on every elevator.
     * Then it lets every elevator in the elevator list tell every human in the listener list
     * that it has moved one Floor.
     */
    public void moveOneFloor() {
        // think about this one, could be more efficient?

        elevators.forEach(Elevator::moveOneFloor);
        // loops through the elevators and moves all the elevators in the arrayList

        elevators.forEach(elevator -> elevatorListeners.forEach(listener -> listener.onElevatorArrivedAtFloor(elevator)));
        // nested loop, looping through all elevators and tells all humans on which floor it has arrived.
        // this part specifically can be done more efficiently as also mentioned in the Javadoc
        // inside the ElevatorListener Interface
        // currentEnteredElevatorId??
    }
}
