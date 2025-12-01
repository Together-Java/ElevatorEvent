package org.togetherjava.event.elevator.models;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

import org.togetherjava.event.elevator.SimulationException;
import org.togetherjava.event.elevator.SimulationUtils;
import org.togetherjava.event.elevator.api.ElevatorPanel;
import org.togetherjava.event.elevator.enums.TravelDirection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * A single elevator that can serve a given amount of floors.
 *
 * <p>An elevator can take floor requests from either humans or the elevator system itself. The
 * elevator will eventually move towards the requested floor and transport humans to their
 * destinations.
 */
@Slf4j
public final class Elevator implements ElevatorPanel {
  private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
  private final int id;
  @Getter private final int minFloor;
  @Getter private final int floorsServed;
  private int currentFloor;
  private final Set<Integer> pendingFloors = new LinkedHashSet<>();
  private Integer activeTarget;

  /**
   * Creates a new elevator.
   *
   * @param minFloor the minimum floor that the elevator can serve, must be greater than or equal to
   *     1.
   * @param floorsServed the amount of floors served in total by this elevator, must be greater than
   *     or equal to 2. Together with the minFloor this forms a consecutive range of floors with no
   *     gaps in between.
   * @param currentFloor the floor the elevator starts at, must be within the defined range of
   *     floors served by the elevator
   */
  public Elevator(int minFloor, int floorsServed, int currentFloor) {
    if (minFloor <= 0 || floorsServed < 2) {
      throw new IllegalArgumentException("Min floor must at least 1, floors served at least 2.");
    }
    if (currentFloor < minFloor || currentFloor >= minFloor + floorsServed) {
      throw new IllegalArgumentException(
          "The current floor must be between the floors served by the elevator.");
    }

    this.id = NEXT_ID.getAndIncrement();

    this.minFloor = minFloor;
    this.currentFloor = currentFloor;
    this.floorsServed = floorsServed;
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public int getCurrentFloor() {
    return this.currentFloor;
  }

  /**
   * Requests this elevator to eventually move to the given floor.
   *
   * <p>This represents a human or the elevator system itself requesting this elevator to move to
   * the given floor. The elevator memorizes the destination to ensure it will eventually reach it.
   *
   * @param destinationFloor the floor to move to, must be within the range of floors served
   * @throws IllegalArgumentException if the destination floor is out of range
   */
  @Override
  public void requestDestinationFloor(int destinationFloor) {

    //    log.info("Requesting elevator {} to reach destination floor {}", this.id,
    // destinationFloor);

    if (destinationFloor < this.minFloor || destinationFloor >= this.minFloor + this.floorsServed) {
      throw new SimulationException("destination outof range for elevator " + this.id);
    }

    if (!this.pendingFloors.contains(destinationFloor)) {

      this.pendingFloors.add(destinationFloor);

      if (this.activeTarget == null) {
        this.activeTarget = destinationFloor;
      }
    }
  }

  /**
   * Moves the elevator one floor towards its target destination, or stands still.
   *
   * <p>The elevator has three possibilities:
   *
   * <ul>
   *   <li>Move up one floor
   *   <li>Move down one floor
   *   <li>Stand still
   * </ul>
   *
   * The elevator moves in a way that eventually reaches all floors requested via {@link
   * #requestDestinationFloor(int)}, ideally fast and fair, meaning that the average time waiting
   * (either in corridor or inside the elevator) is minimized across all humans.
   *
   * <p>This method updates the {@code currentFloor} field accordingly.
   */
  public void moveOneFloor() {

    //    log.debug("mooving elevator {} one floor", this.id);

    if (this.activeTarget == null) {
      if (!this.pendingFloors.isEmpty()) {

        this.activeTarget = this.pendingFloors.iterator().next();

        //        log.debug(
        //            "Elevator {}, has no active target, selecting next pending floor: {}",
        //            this.id,
        //            this.activeTarget);

      } else {
        //        log.debug("elevator {} is idle (no pending floors)", this.id);
        return;
      }
    }

    if (this.currentFloor == this.activeTarget) {
      //      log.debug(
      //          "elevator {} reached target floor {}, marking as complete", this.id,
      // this.activeTarget);

      this.pendingFloors.remove(this.activeTarget);

      this.activeTarget = null;

      if (!this.pendingFloors.isEmpty()) {
        this.activeTarget = this.pendingFloors.iterator().next();
        log.debug("elvator {} selecting next target floor: {}", this.id, this.activeTarget);
      } else {
        log.debug("Elevator {} has no more pending floors", this.id);
        return;
      }
    }

    if (this.currentFloor < this.activeTarget) {
      this.currentFloor++;

      log.debug(
          SimulationUtils.LOG_ELEVATOR_MOVING_FROM_TO,
          this.id,
          TravelDirection.UP,
          this.currentFloor - 1,
          this.currentFloor,
          this.activeTarget);

    } else if (this.currentFloor > this.activeTarget) {

      this.currentFloor--;

      log.debug(
          SimulationUtils.LOG_ELEVATOR_MOVING_FROM_TO,
          this.id,
          TravelDirection.DOWN,
          this.currentFloor + 1,
          this.currentFloor,
          this.activeTarget);
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Elevator.class.getSimpleName() + "[", "]")
        .add("id=" + this.id)
        .add("minFloor=" + this.minFloor)
        .add("floorsServed=" + this.floorsServed)
        .add("currentFloor=" + this.currentFloor)
        .add("pendingFloors=" + this.pendingFloors)
        .add("activeTarget=" + this.activeTarget)
        .toString();
  }
}
