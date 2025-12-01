package org.togetherjava.event.elevator.humans;

import org.togetherjava.event.elevator.elevators.ElevatorPanel;
import org.togetherjava.event.elevator.elevators.FloorPanelSystem;

/**
 * Listeners to elevator events. This is mostly interesting for
 * humans who can then request elevators to move to desired floors.
 */
public interface ElevatorListener {
    /**
     * Fired when the elevator system is ready to receive requests. Elevators can now move.
     *
     * @param floorPanelSystem the system in the corridor that allows
     *                         requesting elevators to the current floor
     */
    void onElevatorSystemReady(FloorPanelSystem floorPanelSystem);

    /**
     * Fired when an elevator arrived at a floor. Humans can now enter or exit if desired.
     *
     * @param elevatorPanel the system inside the elevator which provides information
     *                      about the elevator and can be used to request a destination floor.
     * @implNote The default implementation fires this event from all elevators to all humans, not only to humans that are
     * relevant (i.e. humans that can enter the elevator).
     */
    void onElevatorArrivedAtFloor(ElevatorPanel elevatorPanel);

}
