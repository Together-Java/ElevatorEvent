package org.togetherjava.event.elevator.models;

import java.util.ArrayList;
import java.util.List;

import org.togetherjava.event.elevator.SimulationException;
import org.togetherjava.event.elevator.api.ElevatorListener;
import org.togetherjava.event.elevator.api.FloorPanelSystem;
import org.togetherjava.event.elevator.enums.TravelDirection;

import lombok.extern.slf4j.Slf4j;

/**
 * System controlling all elevators of a building.
 *
 * <p>Once all elevators and humans have been registered via {@link #registerElevator(Elevator)} and
 * {@link #registerElevatorListener(ElevatorListener)} respectively, the system can be made ready
 * using {@link #ready()}.
 */
@Slf4j
public final class ElevatorSystem implements FloorPanelSystem {
  private final List<Elevator> elevators = new ArrayList<>();
  private final List<ElevatorListener> elevatorListeners = new ArrayList<>();

  public ElevatorSystem() {
    log.info("new ElevatorSystem created");
  }

  public void registerElevator(Elevator elevator) {
    log.debug("registering new elevator {}", elevator);
    this.elevators.add(elevator);
  }

  public void registerElevatorListener(ElevatorListener listener) {
    log.debug("registering new listener (human) {}", listener);
    this.elevatorListeners.add(listener);
  }

  /**
   * Upon calling this, the system is ready to receive elevator requests. Elevators may now start
   * moving.
   */
  public void ready() {
    log.info("Starting new Elevator System");
    this.elevatorListeners.forEach(listener -> listener.onElevatorSystemReady(this));
    log.info("Elevator System is ready!");
  }

  /**
   * Requests an elevator for a human standing in the corridor.
   *
   * <p>This represents a human requesting that an elevator comes to pick them up for travel into
   * the given direction. The system makes sure that an elevator will eventually reach this floor to
   * pick up the human. The human can then enter the elevator and request their actual destination
   * within the elevator.
   *
   * <p>The implementation selects the best elevator among all candidates to reduce the time spent
   * waiting (either in the corridor or in the elevator itself).
   *
   * @param atFloor the floor where the human is waiting
   * @param desiredTravelDirection the direction the human wants to travel
   */
  @Override
  public void requestElevator(int atFloor, TravelDirection desiredTravelDirection) {
    //    log.info(
    //        "requesting any close elevator to respond to human at floor {}, going {}",
    //        atFloor,
    //        desiredTravelDirection);

    List<Elevator> staging =
        this.elevators.stream()
            .filter(
                elevator -> {
                  int min = elevator.getMinFloor();
                  return atFloor >= min && atFloor < min + elevator.getFloorsServed();
                })
            .toList();

    if (staging.isEmpty()) {
      throw new SimulationException("No elevators can serve floor " + atFloor);
    }

    Elevator target = staging.getFirst();

    int distanceFlag = Math.abs(atFloor - target.getCurrentFloor());

    for (int i = 1; i < staging.size(); i++) {

      Elevator elevator = staging.get(i);

      final int distance = Math.abs(atFloor - elevator.getCurrentFloor());

      if (distance < distanceFlag
          || (distance == distanceFlag && elevator.getId() < target.getId())) {

        target = elevator;
        distanceFlag = distance;
      }
    }

    //    log.info("selected elevator {}", target.getId());

    target.requestDestinationFloor(atFloor);

    //    log.info(
    //        "Selected elevator {} at floor {} for pickup at floor {} (distance {}) direction {}",
    //        target.getId(),
    //        target.getCurrentFloor(),
    //        atFloor,
    //        distanceFlag,
    //        desiredTravelDirection);
  }

  public void moveOneFloor() {

    //    log.info("movement order started");

    this.elevators.forEach(Elevator::moveOneFloor);

    //    log.info("dispatching arrived at floor events to all listeners per elevator");

    this.elevators.forEach(
        elevator ->
            this.elevatorListeners.forEach(
                listener -> listener.onElevatorArrivedAtFloor(elevator)));

    //    log.info("movement order finished");
  }
}
