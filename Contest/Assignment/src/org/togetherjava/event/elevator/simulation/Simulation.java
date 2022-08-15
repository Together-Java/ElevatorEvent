package org.togetherjava.event.elevator.simulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.togetherjava.event.elevator.elevators.CommonElevator;
import org.togetherjava.event.elevator.elevators.Elevator;
import org.togetherjava.event.elevator.elevators.ElevatorSystem;
import org.togetherjava.event.elevator.humans.Human;
import org.togetherjava.event.elevator.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Simulation {
    private static final Logger logger  = LogManager.getLogger();

    protected final List<Human> humans;
    protected final List<Elevator> elevators;
    protected final ElevatorSystem elevatorSystem;
    private final View view;
    protected long stepCount;
    protected final List<HumanStatistics> humanStatistics;

    public static Simulation createSingleElevatorSingleHumanSimulation() {
        return new Simulation(List.of(new CommonElevator(1, 10, 5)),
                List.of(new Human(1, 10)));
    }

    public static Simulation createSimpleSimulation() {
        int minFloor = 1;
        int floorsServed = 10;

        return new Simulation(
                List.of(
                        new CommonElevator(minFloor, floorsServed, 1),
                        new CommonElevator(minFloor, floorsServed, 6)),
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
        return createRandomSimulation(seed, amountOfElevators, amountOfHumans, floorsServed, Simulation::new);
    }

    public static <S extends Simulation> S createRandomSimulation(long seed, int amountOfElevators, int amountOfHumans, int floorsServed, SimulationFactory<S> factory) {
        logger.info("Seed for random simulation is: " + seed);
        Random random = new Random(seed);

        int minFloor = 1;

        List<? extends Elevator> elevators = Stream.generate(() -> {
            int currentFloor = minFloor + random.nextInt(floorsServed);
            return new CommonElevator(minFloor, floorsServed, currentFloor);
        }).limit(amountOfElevators).toList();

        List<Human> humans = Stream.generate(() -> {
            int startingFloor = minFloor + random.nextInt(floorsServed);
            int destinationFloor = minFloor + random.nextInt(floorsServed);
            return new Human(startingFloor, destinationFloor);
        }).limit(amountOfHumans).toList();

        return factory.createSimulation(elevators, humans);
    }

    interface SimulationFactory<S extends Simulation> {
        S createSimulation(List<? extends Elevator> elevators, List<Human> humans);
    }

    public Simulation(List<? extends Elevator> elevators, List<Human> humans) {
        this.elevators = new ArrayList<>(elevators);
        this.humans = new ArrayList<>(humans);

        elevatorSystem = new ElevatorSystem();
        this.elevators.forEach(elevatorSystem::registerElevator);
        this.humans.forEach(elevatorSystem::registerElevatorListener);

        humanStatistics = this.humans.stream().map(HumanStatistics::new).collect(Collectors.toCollection(() -> new ArrayList<>(humans.size())));
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
        LogUtils.measure("Ready", elevatorSystem::ready);
    }

    public void step() {
        elevatorSystem.moveOneFloor();
        LogUtils.measure("Statistics update", () -> humanStatistics.forEach(HumanStatistics::step));
        stepCount++;
    }

    public boolean isDone() {
//        return humans.stream()
//                .map(Human::getCurrentState)
//                .allMatch(Human.State.ARRIVED::equals);
        return !elevatorSystem.hasActivePassengers();
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

    public void printCurrentStatistics() {
        logger.trace(() -> humanStatistics.stream()
                .collect(Collectors.toMap((HumanStatistics stat) -> stat.getHuman().getCurrentState(), s -> 1, Integer::sum)));
    }

    public boolean shouldPrintSummary() {
        return elevators.size() <= 100 && humans.size() <= 100;
    }

    public boolean shouldPrint() {
        return elevatorSystem.getFloorAmount() <= 10 && elevators.size() <= 20;
    }
}
