package org.togetherjava.event.elevator;

import org.togetherjava.event.elevator.models.Simulation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Main {

  static void main() {
    try {
      Simulation simulation = SimulationService.createRandomSimulation(2, 4, 4);

      SimulationUtils.printSummary(simulation);

      SimulationService.start(simulation);

      SimulationUtils.prettyPrint(simulation);

      final int stepLimit = 100_000;

      while (!SimulationService.isDone(simulation)) {
        log.info("\tSimulation step {}", simulation.getStepCount());
        SimulationService.step(simulation);
        SimulationUtils.prettyPrint(simulation);

        if (simulation.getStepCount() >= stepLimit) {
          throw new SimulationException(
              "Simulation aborted. All humans should have arrived by now, but they did not. There is likely a bug in your code.");
        }
      }
      log.info("Elevator Simulation is done.");

      SimulationUtils.printResult(simulation);

    } catch (SimulationException ex) {
      log.error("Elevator Simulation error: {}", ex.getMessage());
    }
  }
}
