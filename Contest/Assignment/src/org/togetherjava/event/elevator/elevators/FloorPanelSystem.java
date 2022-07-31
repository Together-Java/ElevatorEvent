package org.togetherjava.event.elevator.elevators;

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
     * @apiNote This represents a human standing in the corridor, pressing a button on the wall,
     * requesting that an elevator comes to pick them up for travel into the given direction.
     */
    void requestElevator(int atFloor, TravelDirection desiredTravelDirection);
}
