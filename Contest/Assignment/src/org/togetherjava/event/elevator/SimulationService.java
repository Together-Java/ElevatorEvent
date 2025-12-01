package org.togetherjava.event.elevator;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.togetherjava.event.elevator.enums.HumanState;
import org.togetherjava.event.elevator.models.Elevator;
import org.togetherjava.event.elevator.models.Human;
import org.togetherjava.event.elevator.models.HumanStatistics;
import org.togetherjava.event.elevator.models.Simulation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SimulationService {
  private SimulationService() {
    throw new UnsupportedOperationException(SimulationUtils.CANNOT_INSTANTIATE_UTILITY_CLASS);
  }

  public static Simulation createSingleElevatorSingleHumanSimulation() {
    return new Simulation(List.of(new Elevator(1, 10, 5)), List.of(new Human(1, 10)));
  }

  public static Simulation createSimpleSimulation() {
    int minFloor = 1;
    int floorsServed = 10;

    return new Simulation(
        List.of(new Elevator(minFloor, floorsServed, 1), new Elevator(minFloor, floorsServed, 6)),
        List.of(
            new Human(1, 2), new Human(1, 5), new Human(8, 10), new Human(9, 3), new Human(10, 1)));
  }

  public static Simulation createRandomSimulation(
      int amountOfElevators, int amountOfHumans, int floorsServed) {
    return createRandomSimulation(
        ThreadLocalRandom.current().nextLong(), amountOfElevators, amountOfHumans, floorsServed);
  }

  public static Simulation createRandomSimulation(
      long seed, int amountOfElevators, int amountOfHumans, int floorsServed) {

    log.debug("Seed for random simulation is: {}", seed);

    //    log.info(
    //        "creating random simulation: elevatorsCount {}, humansCount {}, floorsServed {}",
    //        amountOfElevators,
    //        amountOfHumans,
    //        floorsServed);

    Random random = new Random(seed);

    final int minFloor = 1;

    List<Elevator> elevators =
        Stream.generate(
                () -> {
                  int currentFloor = minFloor + random.nextInt(floorsServed);
                  return new Elevator(minFloor, floorsServed, currentFloor);
                })
            .limit(amountOfElevators)
            .toList();

    List<Human> humans =
        Stream.generate(
                () -> {
                  int startingFloor = minFloor + random.nextInt(floorsServed);
                  int destinationFloor = minFloor + random.nextInt(floorsServed);
                  return new Human(startingFloor, destinationFloor);
                })
            .limit(amountOfHumans)
            .toList();

    return new Simulation(elevators, humans);
  }

  public static void start(Simulation simulation) {
    //    log.info("starting new simulation...");
    simulation.getElevatorSystem().ready();
    //    log.info("Simulation is ready");
  }

  public static void step(Simulation simulation) {
    //    log.info("step started");

    simulation.getElevatorSystem().moveOneFloor();

    simulation.getHumanStatistics().forEach(HumanStatistics::step);

    simulation.increaseStepCount();

    //    log.info("step finished!");
  }

  public static boolean isDone(Simulation simulation) {
    List<Human> humans = simulation.getHumans();
    return humans.stream().map(Human::getCurrentState).allMatch(HumanState.ARRIVED::equals);
  }
}
