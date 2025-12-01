package org.togetherjava.event.elevator.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.togetherjava.event.elevator.enums.HumanState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class View {
  private final Simulation simulation;
  private static final int ELEVATOR_WIDTH = 7;
  private static final int CORRIDOR_WIDTH = 9;
  private static final int FLOOR_LABEL_WIDTH = 4;

  public View(Simulation simulation) {
    this.simulation = simulation;
  }

  public void printSummary() {
    log.info(
        "Simulation with {} elevators and {} humans.",
        this.simulation.getElevators().size(),
        this.simulation.getHumans().size());
    log.debug("\tElevators: {}", this.simulation.getElevators());
    log.debug("\tHumans: {}", this.simulation.getHumans());
  }

  public void prettyPrint() {
    int totalFloors =
        this.simulation.getElevators().stream()
            .mapToInt(elevator -> elevator.getMinFloor() + elevator.getFloorsServed() - 1)
            .max()
            .orElseThrow();
    Map<OptionalInt, Long> elevatorIdToHumansCount =
        this.simulation.getHumans().stream()
            .collect(
                Collectors.groupingBy(Human::getCurrentEnteredElevatorId, Collectors.counting()));

    log.info(
        "{}",
        " ".repeat(FLOOR_LABEL_WIDTH)
            + "_"
                .repeat(
                    1 + CORRIDOR_WIDTH + ELEVATOR_WIDTH * this.simulation.getElevators().size() + 1)
            + " ");
    for (int floor = totalFloors; floor >= 1; floor--) {
      this.printFloor(floor, elevatorIdToHumansCount);
    }
    log.info(
        " ".repeat(FLOOR_LABEL_WIDTH - 2)
            + "^"
                .repeat(
                    2
                        + 2
                        + CORRIDOR_WIDTH
                        + ELEVATOR_WIDTH * this.simulation.getElevators().size()
                        + 2
                        + 2));
  }

  private void printFloor(int floor, Map<OptionalInt, Long> elevatorIdToHumansCount) {
    String emptyLeftLine = " ".repeat(FLOOR_LABEL_WIDTH) + "| ";
    List<String> leftLines = List.of(emptyLeftLine, "%3s | ".formatted(floor), emptyLeftLine);

    String emptyRightLine = " |";
    List<String> rightLines = List.of(emptyRightLine, emptyRightLine, emptyRightLine);

    List<String> corridorLines = this.corridorForFloorToLines(floor);

    List<List<String>> allElevatorLines =
        this.simulation.getElevators().stream()
            .map(
                elevator ->
                    this.elevatorForFloorToLines(
                        floor,
                        elevator,
                        elevatorIdToHumansCount.getOrDefault(OptionalInt.of(elevator.getId()), 0L)))
            .toList();

    List<List<String>> allLines = new ArrayList<>();
    allLines.add(leftLines);
    allLines.add(corridorLines);
    allLines.addAll(allElevatorLines);
    allLines.add(rightLines);

    IntFunction<String> mergeRow =
        row -> allLines.stream().map(lines -> lines.get(row)).collect(Collectors.joining());

    log.info("{}", mergeRow.apply(0));
    log.info("{}", mergeRow.apply(1));
    log.info("{}", mergeRow.apply(2));
  }

  private List<String> elevatorForFloorToLines(
      int floor, Elevator elevator, long humansInElevator) {
    if (floor != elevator.getCurrentFloor()) {
      String emptyLine = " ".repeat(ELEVATOR_WIDTH / 2) + "." + " ".repeat(ELEVATOR_WIDTH / 2);
      return List.of(emptyLine, emptyLine, emptyLine);
    }

    String humansInElevatorText = humansInElevator == 0 ? "" : Long.toString(humansInElevator);

    return List.of(
        " " + "_".repeat(ELEVATOR_WIDTH - 2) + " ",
        "| %3s |".formatted(humansInElevatorText),
        " -" + "%3s".formatted(elevator.getId()).replace(' ', '-') + "- ");
  }

  private List<String> corridorForFloorToLines(int floor) {
    long humansArrived =
        this.simulation.getHumans().stream()
            .filter(human -> human.getDestinationFloor() == floor)
            .map(Human::getCurrentState)
            .filter(HumanState.ARRIVED::equals)
            .count();
    long humansWaiting =
        this.simulation.getHumans().stream()
            .filter(human -> human.getStartingFloor() == floor)
            .map(Human::getCurrentState)
            .filter(
                state ->
                    state.equals(HumanState.IDLE) || state.equals(HumanState.WAITING_FOR_ELEVATOR))
            .count();

    return List.of(
        " %3s A | ".formatted(humansArrived),
        " %3s W   ".formatted(humansWaiting),
        "~".repeat(CORRIDOR_WIDTH - 2) + "| ");
  }
}
