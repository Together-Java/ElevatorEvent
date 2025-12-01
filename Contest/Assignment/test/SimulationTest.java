import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.togetherjava.event.elevator.SimulationService;
import org.togetherjava.event.elevator.models.Simulation;

final class SimulationTest {
  private boolean simulationFailed;
  private static boolean stopSimulations = false;

  @BeforeEach
  void setUp() {
    assumeFalse(stopSimulations, "A previous simulation failed already, skipping the remaining.");

    this.simulationFailed = true;
  }

  @AfterEach
  void tearDown() {
    if (this.simulationFailed) {
      stopSimulations = true;
    }
  }

  @Test
  void testSingleElevatorSingleHumanSimulation() {
    Simulation simulation = SimulationService.createSingleElevatorSingleHumanSimulation();
    int stepLimit = 200;

    assertDoesNotThrow(
        () -> startAndExecuteUntilDone(simulation, stepLimit),
        "Simulation obtained by 'SimulationService.createSingleElevatorSingleHumanSimulation()' was aborted because it could not finish in time.");

    this.simulationFailed = false;
  }

  @Test
  void testSimpleSimulation() {
    Simulation simulation = SimulationService.createSimpleSimulation();
    int stepLimit = 500;

    assertDoesNotThrow(
        () -> startAndExecuteUntilDone(simulation, stepLimit),
        "Simulation obtained by 'SimulationService.createSimpleSimulation()' was aborted because it could not finish in time.");

    this.simulationFailed = false;
  }

  @Test
  void testRandomSimulationSmall() {
    Simulation simulation = SimulationService.createRandomSimulation(1, 5, 50, 10);
    int stepLimit = 1_000;

    assertDoesNotThrow(
        () -> startAndExecuteUntilDone(simulation, stepLimit),
        "Simulation obtained by 'SimulationService.createRandomSimulation(1, 5, 50, 10)' was aborted because it could not finish in time.");

    this.simulationFailed = false;
  }

  @Test
  void testRandomSimulationMedium() {
    Simulation simulation = SimulationService.createRandomSimulation(2, 20, 1_000, 50);
    int stepLimit = 10_000;

    assertDoesNotThrow(
        () -> startAndExecuteUntilDone(simulation, stepLimit),
        "Simulation obtained by 'SimulationService.createRandomSimulation(2, 20, 1_000, 50)' was aborted because it could not finish in time.");

    this.simulationFailed = false;
  }

  @Test
  void testRandomSimulationBig() {
    Simulation simulation = SimulationService.createRandomSimulation(3, 100, 100_000, 100);
    int stepLimit = 100_000;

    assertDoesNotThrow(
        () -> startAndExecuteUntilDone(simulation, stepLimit),
        "Simulation obtained by 'SimulationService.createRandomSimulation(3, 100, 100_000, 100)' was aborted because it could not finish in time.");

    this.simulationFailed = false;
  }

  private static void startAndExecuteUntilDone(Simulation simulation, int stepLimit) {
    SimulationService.start(simulation);

    while (!SimulationService.isDone(simulation)) {
      SimulationService.step(simulation);
      if (simulation.getStepCount() >= stepLimit) {
        throw new IllegalStateException(
            "Simulation aborted. All humans should have arrived by now, but they did not. There is likely a bug in your code.");
      }
    }
  }
}
