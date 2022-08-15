package org.togetherjava.event.elevator.elevators;

import org.togetherjava.event.elevator.humans.ElevatorListener;

/**
 * The system in corridors that allows requesting elevators to the current floor.
 */
public interface FloorPanelSystem {
    /**
     * Requests an elevator to move to the given floor to pick up a human.
     *
     * @param atFloor                the floor to pick up the human at, must be within the range served by the system
     * @param desiredTravelDirection the direction the human wants to travel into,
     *                               can be used for determination of the best elevator
     * @param listener               (NEW) the listener that requested the operation
     * @return the id of the elevator that was recommended by the system
     * @apiNote This represents a human standing in the corridor, pressing a button on the wall,
     * requesting that an elevator comes to pick them up for travel into the given direction.
     */
    int requestElevator(int atFloor, TravelDirection desiredTravelDirection, ElevatorListener listener);
}
