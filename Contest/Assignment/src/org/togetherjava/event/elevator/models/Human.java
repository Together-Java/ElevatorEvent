package org.togetherjava.event.elevator.models;

import java.util.OptionalInt;
import java.util.StringJoiner;

import org.togetherjava.event.elevator.api.ElevatorListener;
import org.togetherjava.event.elevator.api.ElevatorPanel;
import org.togetherjava.event.elevator.api.FloorPanelSystem;
import org.togetherjava.event.elevator.enums.HumanState;
import org.togetherjava.event.elevator.enums.TravelDirection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * A single human that starts at a given floor and wants to reach a destination floor via an
 * elevator.
 *
 * <p>The class mainly acts upon given elevator events it listens to, for example requesting an
 * elevator, eventually entering and exiting them.
 */
@Slf4j
public final class Human implements ElevatorListener {
  @Getter private HumanState currentState;
  @Getter private final int startingFloor;
  @Getter private final int destinationFloor;

  /**
   * If the human is currently inside an elevator, this is its unique ID. Otherwise, this is {@code
   * null} to indicate that the human is currently on the corridor.
   */
  private Integer currentEnteredElevatorId;

  /**
   * Creates a new human.
   *
   * <p>It is supported that starting and destination floors are equal. The human will then not
   * travel with an elevator at all.
   *
   * @param startingFloor the floor the human currently stands at, must be greater than or equal to
   *     1
   * @param destinationFloor the floor the human eventually wants to reach, must be greater than or
   *     equal to 1
   */
  public Human(int startingFloor, int destinationFloor) {
    if (startingFloor <= 0 || destinationFloor <= 0) {
      throw new IllegalArgumentException("Floors must be at least 1");
    }
    this.startingFloor = startingFloor;
    this.destinationFloor = destinationFloor;
    this.currentState = HumanState.IDLE;
  }

  /**
   * Invoked once the elevator system is ready to accept requests. Transitions the human out of the
   * IDLE state. If the starting and destination floors are identical, the human is marked as
   * arrived immediately. Otherwise, the correct travel direction is determined, an elevator is
   * requested at the starting floor, and the state changes to WAITING_FOR_ELEVATOR.
   *
   * @param floorPanelSystem panel system used to request an elevator
   */
  @Override
  public void onElevatorSystemReady(FloorPanelSystem floorPanelSystem) {

    final int start = this.startingFloor;

    log.info("checking human at floor {}, state", start);

    final int destination = this.destinationFloor;

    if (start == destination) {

      final HumanState state = HumanState.ARRIVED;

      log.info("starting floor is the same as destination floor => marking state as {}", state);

      this.currentState = state;

      return;
    }

    TravelDirection direction = start < destination ? TravelDirection.UP : TravelDirection.DOWN;

    log.info("computed direction for human at floor {} is {}", start, direction);

    floorPanelSystem.requestElevator(start, direction);

    HumanState newState = HumanState.WAITING_FOR_ELEVATOR;

    log.info("updating human at floor {} state from {} to {}", start, this.currentState, newState);

    this.currentState = newState;

    log.info(" human at floor {} state updated", start);

    log.info(
        "human startin from floor {}, requesting elevator towards floor {}, direction {}",
        start,
        destination,
        direction);
  }

  /**
   * Handles the event when an elevator arrives at a floor.
   *
   * <p>If the human is currently waiting for an elevator and this event represents arrival at the
   * human's current floor, the human can now enter the elevator and request their actual
   * destination floor. The state has to change to TRAVELING_WITH_ELEVATOR.
   *
   * <p>If the human is currently traveling with this elevator and the event represents arrival at
   * the human's destination floor, the human can now exit the elevator.
   *
   * @param elevatorPanel the panel of the elevator that arrived at a floor
   */
  @Override
  public void onElevatorArrivedAtFloor(ElevatorPanel elevatorPanel) {
    final int elevatorId = elevatorPanel.getId();
    final int elevatorCurrentFloor = elevatorPanel.getCurrentFloor();

    //    log.debug(
    //        "human at floor {}, (state: {}) received elevator {} arrival at floor {}",
    //        this.startingFloor,
    //        this.currentState,
    //        elevatorId,
    //        elevatorCurrentFloor);

    if (this.currentState == HumanState.ARRIVED) {
      //      log.debug("human already arrived! ignoring elevator arrival");
      return;
    }

    // humanDestinationFloor     = this.destinationFloor;

    if (this.currentState == HumanState.WAITING_FOR_ELEVATOR
        && elevatorCurrentFloor == this.startingFloor) {

      //      log.debug(
      //          "uman at floor {}, enters elevator {}, to travel to floor {}",
      //          this.startingFloor,
      //          elevatorId,
      //          this.destinationFloor);

      this.currentEnteredElevatorId = elevatorId;

      this.currentState = HumanState.TRAVELING_WITH_ELEVATOR;

      elevatorPanel.requestDestinationFloor(this.destinationFloor);

      //      log.debug("human entered elevator {}, state changed to {}", elevatorId,
      // this.currentState);

      return;
    }

    if (this.currentState == HumanState.TRAVELING_WITH_ELEVATOR
        && this.currentEnteredElevatorId != null
        && this.currentEnteredElevatorId == elevatorId
        && elevatorCurrentFloor == this.destinationFloor) {

      //      log.debug(
      //          "human is in elevator {}, has reached destination floor {}, exiting",
      //          elevatorId,
      //          this.destinationFloor);

      this.currentEnteredElevatorId = null;

      this.currentState = HumanState.ARRIVED;

      //      log.debug(
      //          "human exited elevator {}, the state now becomes {}", elevatorId,
      // this.currentState);
    }
  }

  public OptionalInt getCurrentEnteredElevatorId() {
    return this.currentEnteredElevatorId == null
        ? OptionalInt.empty()
        : OptionalInt.of(this.currentEnteredElevatorId);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Human.class.getSimpleName() + "[", "]")
        .add("currentState=" + this.currentState)
        .add("startingFloor=" + this.startingFloor)
        .add("destinationFloor=" + this.destinationFloor)
        .add("currentEnteredElevatorId=" + this.currentEnteredElevatorId)
        .toString();
  }
}
