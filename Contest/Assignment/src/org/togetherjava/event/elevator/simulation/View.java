package org.togetherjava.event.elevator.simulation;

import org.togetherjava.event.elevator.elevators.Elevator;
import org.togetherjava.event.elevator.humans.Human;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public final class View {
    private final Simulation simulation;
    private static final int ELEVATOR_WIDTH = 7;
    private static final int CORRIDOR_WIDTH = 9;
    private static final int FLOOR_LABEL_WIDTH = 4;

    public View(Simulation simulation) {
        this.simulation = simulation;
    }

    public void printSummary() {
        System.out.printf("Simulation with %d elevators and %d humans.%n", simulation.getElevators().size(), simulation.getHumans().size());
        System.out.println("\tElevators: " + simulation.getElevators());
        System.out.println("\tHumans: " + simulation.getHumans());
    }

    public void prettyPrint() {
        int totalFloors = simulation.getElevators().stream()
                .mapToInt(elevator -> elevator.getMinFloor() + elevator.getFloorsServed() - 1)
                .max()
                .orElseThrow();
        Map<OptionalInt, Long> elevatorIdToHumansCount = simulation.getHumans().stream()
                .collect(Collectors.groupingBy(Human::getCurrentEnteredElevatorId,
                        Collectors.counting()));

        printRoof();
        for (int floor = totalFloors; floor >= 1; floor--) {
            printFloor(floor, elevatorIdToHumansCount);
        }
        printBasement();
    }

    private void printRoof() {
        System.out.println(" ".repeat(FLOOR_LABEL_WIDTH)
                + " " + "_".repeat(1 + CORRIDOR_WIDTH + ELEVATOR_WIDTH * simulation.getElevators().size() + 1) + " ");
    }

    private void printBasement() {
        System.out.println(" ".repeat(FLOOR_LABEL_WIDTH - 2)
                + "^".repeat(2 + 2 + CORRIDOR_WIDTH + ELEVATOR_WIDTH * simulation.getElevators().size() + 2 + 2));
    }

    private void printFloor(int floor, Map<OptionalInt, Long> elevatorIdToHumansCount) {
        String emptyLeftLine = " ".repeat(FLOOR_LABEL_WIDTH) + "| ";
        List<String> leftLines = List.of(emptyLeftLine,
                "%3s | ".formatted(floor),
                emptyLeftLine);

        String emptyRightLine = " |";
        List<String> rightLines = List.of(emptyRightLine, emptyRightLine, emptyRightLine);

        List<String> corridorLines = corridorForFloorToLines(floor);

        List<List<String>> allElevatorLines = simulation.getElevators().stream()
                .map(elevator -> elevatorForFloorToLines(floor, elevator,
                        elevatorIdToHumansCount.getOrDefault(OptionalInt.of(elevator.getId()), 0L)))
                .toList();

        List<List<String>> allLines = new ArrayList<>();
        allLines.add(leftLines);
        allLines.add(corridorLines);
        allLines.addAll(allElevatorLines);
        allLines.add(rightLines);

        IntFunction<String> mergeRow = row -> allLines.stream().map(lines -> lines.get(row)).collect(Collectors.joining());

        System.out.println(mergeRow.apply(0));
        System.out.println(mergeRow.apply(1));
        System.out.println(mergeRow.apply(2));
    }

    private List<String> elevatorForFloorToLines(int floor, Elevator elevator, long humansInElevator) {
        if (floor != elevator.getCurrentFloor()) {
            String emptyLine = " ".repeat(ELEVATOR_WIDTH / 2) + "." + " ".repeat(ELEVATOR_WIDTH / 2);
            return List.of(emptyLine, emptyLine, emptyLine);
        }

        String humansInElevatorText = humansInElevator == 0 ? "" : Long.toString(humansInElevator);

        return List.of(" " + "_".repeat(ELEVATOR_WIDTH - 2) + " ",
                "| %3s |".formatted(humansInElevatorText),
                " -" + "%3s".formatted(elevator.getId()).replace(' ', '-') + "- ");
    }

    private List<String> corridorForFloorToLines(int floor) {
        long humansArrived = simulation.getHumans().stream()
                .filter(human -> human.getDestinationFloor() == floor)
                .map(Human::getCurrentState)
                .filter(Human.State.ARRIVED::equals)
                .count();
        long humansWaiting = simulation.getHumans().stream()
                .filter(human -> human.getStartingFloor() == floor)
                .map(Human::getCurrentState)
                .filter(state -> state.equals(Human.State.IDLE) || state.equals(Human.State.WAITING_FOR_ELEVATOR))
                .count();

        return List.of(" %3s A | ".formatted(humansArrived),
                " %3s W   ".formatted(humansWaiting),
                "~".repeat(CORRIDOR_WIDTH - 2) + "| ");
    }
}
