package org.togetherjava.event.elevator.simulation;

import org.togetherjava.event.elevator.elevators.CommonElevator;
import org.togetherjava.event.elevator.elevators.PaternosterElevator;
import org.togetherjava.event.elevator.humans.Human;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Separate file to avoid bloating {@link Simulation} with static methods
 */
public class MoreSimulations {

    public static Simulation createSimpleFailingSimulation() {
        return new Simulation(
                List.of(
                        new CommonElevator(6, 5, 10),
                        new CommonElevator(1, 5, 5)),
                List.of(
                        new Human(1, 7)));
    }

    public static Simulation createSimpleSucceedingSimulation() {
        return new Simulation(
                List.of(
                        new CommonElevator(6, 5, 10),
                        new CommonElevator(1, 6, 5)),
                List.of(
                        new Human(1, 8)));
    }

    public static Simulation createSimpleThreeStepSimulation() {
        return new Simulation(
                List.of(
                        new CommonElevator(7, 4, 10),
                        new CommonElevator(4, 5, 5),
                        new CommonElevator(1, 5, 3)),
                List.of(
                        new Human(1, 10)));
    }

    public static Simulation createSimplePaternosterSimulation() {
        return new Simulation(
                List.of(
                        new CommonElevator(6, 5, 10),
                        new PaternosterElevator(1, 8, 5),
                        new PaternosterElevator(8, 3, 10)),
                List.of(
                        new Human(1, 7)));
    }

    public static AdvancedSimulation createSimpleAdvancedSimulation() {
        return new AdvancedSimulation(
                List.of(
                        new CommonElevator(1, 10, 10),
                        new CommonElevator(1, 10, 8)),
                List.of(
                        new Human(1, 9)));
    }

    public static Simulation createMegaSimulation() {
        return Simulation.createRandomSimulation(3, 100, 100_000, 100);
    }

    public static AdvancedSimulation createMegaAdvancedSimulation() {
        return AdvancedSimulation.createRandomSimulation(3, 100, 100_000, 100);
    }

    public static Simulation createNotMarkoSimulation() {
        return Simulation.createRandomSimulation(2, 4, 1_000, 50);
    }

    public static Simulation createGoodPaternosterSimulation() {
        return new Simulation(
                IntStream.rangeClosed(1, 4).mapToObj(i -> new PaternosterElevator(1, 50, (int) Math.ceil(Math.random() * 50))).toList(),
                IntStream.rangeClosed(1, 1_000).mapToObj(i -> new Human((int) Math.ceil(Math.random() * 50), (int) Math.ceil(Math.random() * 50))).toList()
        );
    }
}
