package org.togetherjava.event.elevator.simulation;

import org.togetherjava.event.elevator.elevators.Elevator;
import org.togetherjava.event.elevator.elevators.ElevatorSystem;
import org.togetherjava.event.elevator.humans.Human;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final class Simulation {
    private final List<Human> humans;
    private final List<Elevator> elevators;
    private final ElevatorSystem elevatorSystem;
    private final View view;
    private long stepCount;
    private final List<HumanStatistics> humanStatistics;

    public static Simulation createSingleElevatorSingleHumanSimulation() {
        return new Simulation(List.of(new Elevator(1, 10, 5)),
                List.of(new Human(1, 10)));
    }

    public static Simulation createSimpleSimulation() {
        int minFloor = 1;
        int floorsServed = 10;

        return new Simulation(
                List.of(
                        new Elevator(minFloor, floorsServed, 1),
                        new Elevator(minFloor, floorsServed, 6)),
                List.of(
                        new Human(1, 2),
                        new Human(1, 5),
                        new Human(8, 10),
                        new Human(9, 3),
                        new Human(10, 1)));
    }

    public static Simulation createRandomSimulation(int amountOfElevators, int amountOfHumans, int floorsServed) {
        return createRandomSimulation(ThreadLocalRandom.current().nextLong(), amountOfElevators, amountOfHumans, floorsServed);
    }

    public static Simulation createRandomSimulation(long seed, int amountOfElevators, int amountOfHumans, int floorsServed) {
        System.out.println("Seed for random simulation is: " + seed);
        Random random = new Random(seed);

        int minFloor = 1;

        List<Elevator> elevators = Stream.generate(() -> {
            int currentFloor = minFloor + random.nextInt(floorsServed);
            return new Elevator(minFloor, floorsServed, currentFloor);
        }).limit(amountOfElevators).toList();

        List<Human> humans = Stream.generate(() -> {
            int startingFloor = minFloor + random.nextInt(floorsServed);
            int destinationFloor = minFloor + random.nextInt(floorsServed);
            return new Human(startingFloor, destinationFloor);
        }).limit(amountOfHumans).toList();

        return new Simulation(elevators, humans);
    }
    public static Simulation createCustomSimulation() {
        return new Simulation(List.of(new Elevator(1, 10, 10)),
                List.of(new Human(2, 2),new Human(1, 2)));
    }

    public Simulation(List<Elevator> elevators, List<Human> humans) {
        this.elevators = new ArrayList<>(elevators);
        this.humans = new ArrayList<>(humans);

        elevatorSystem = new ElevatorSystem();
        this.elevators.forEach(elevatorSystem::registerElevator);
        this.humans.forEach(elevatorSystem::registerElevatorListener);

        humanStatistics = this.humans.stream().map(HumanStatistics::new).toList();
        view = new View(this);
    }

    public void startAndExecuteUntilDone(int stepLimit) {
        start();

        while (!isDone()) {
            step();

            if (stepCount >= stepLimit) {
                throw new IllegalStateException("Simulation aborted. All humans should have arrived"
                        + " by now, but they did not. There is likely a bug in your code.");
            }
        }
    }

    public void start() {
        elevatorSystem.ready();
    }

    public void step() {
        elevatorSystem.moveOneFloor();
        humanStatistics.forEach(HumanStatistics::step);
        stepCount++;
    }

    public boolean isDone() {
        return humans.stream()
                .map(Human::getCurrentState)
                .allMatch(Human.State.ARRIVED::equals);
    }

    public long getStepCount() {
        return stepCount;
    }

    public List<Human> getHumans() {
        return Collections.unmodifiableList(humans);
    }

    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(elevators);
    }

    public ElevatorSystem getElevatorSystem() {
        return elevatorSystem;
    }

    public void printSummary() {
        view.printSummary();
    }

    public void prettyPrint() {
        view.prettyPrint();
    }

    public void printResult() {
        System.out.println("Steps: " + stepCount);

        System.out.println("Median time spend per state:");
        for (Human.State state : Human.State.values()) {
            int averagePercentage = getAverageTimePercentageSpendForState(state);
            System.out.printf("\t%s: %d%%%n", state, averagePercentage);
        }
    }

    public int getAverageTimePercentageSpendForState(Human.State state) {
        LongStream sortedSteps = humanStatistics.stream()
                .mapToLong(stats -> stats.stepsForState(state))
                .sorted();
        long medianSteps = humanStatistics.size() % 2 == 0
                ? (long) sortedSteps.skip(humanStatistics.size() / 2 - 1).limit(2).average().orElseThrow()
                : sortedSteps.skip(humanStatistics.size() / 2).findFirst().orElseThrow();

        long medianPercentage = 100 * medianSteps / stepCount;
        return (int) medianPercentage;
    }
}
