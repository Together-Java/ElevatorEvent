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
    private final List<Elevator> elevators= new ArrayList<>();
    private final List<ElevatorListener> elevatorListeners= new ArrayList<>();

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

    private boolean isInRange(int floor,Elevator elevator)
    {
        return floor>=elevator.getMinFloor() && floor<=elevator.getMaxFloor();
    }
    private Elevator getClosestElevator(int atFloor)
    {
        int minimumFloorDifference=Math.abs(elevators.get(0).getCurrentFloor()-atFloor);
        if(minimumFloorDifference==0)
            return elevators.get(0);
        Elevator closestElevator=elevators.get(0);
        for(Elevator elevator:elevators)
        {
            int currentFloor=elevator.getCurrentFloor();
            int floorDifference=Math.abs(currentFloor-atFloor);
            if(floorDifference<minimumFloorDifference&&isInRange(currentFloor,elevator))
            {
                minimumFloorDifference=floorDifference;
                closestElevator=elevator;
            }
        }
        return closestElevator;
    }
    @Override
    public void requestElevator(int atFloor, TravelDirection desiredTravelDirection) {
        // TODO Implement. This represents a human standing in the corridor,
        //  requesting that an elevator comes to pick them up for travel into the given direction.
        //  The system is supposed to make sure that an elevator will eventually reach this floor to pick up the human.
        //  The human can then enter the elevator and request their actual destination within the elevator.
        //  Ideally this has to select the best elevator among all which can reduce the time
        //  for the human spending waiting (either in corridor or in the elevator itself).
        Elevator chosenElevator= getClosestElevator(atFloor);
        if(chosenElevator!=null)
            chosenElevator.requestDestinationFloor(atFloor);
    }

    public void moveOneFloor() {
        elevators.forEach(Elevator::moveOneFloor);
        elevators.forEach(elevator -> elevatorListeners.forEach(listener -> listener.onElevatorArrivedAtFloor(elevator)));
    }
}
