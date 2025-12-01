import org.junit.jupiter.api.*;
import org.togetherjava.event.elevator.simulation.Simulation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

final class SimulationTest {
    private boolean simulationFailed;
    private static boolean stopSimulations = false;

    @BeforeEach
    void setUp() {
        assumeFalse(stopSimulations,
                "A previous simulation failed already, skipping the remaining.");

        simulationFailed = true;
    }

    @AfterEach
    void tearDown() {
        if (simulationFailed) {
            stopSimulations = true;
        }
    }

    @Test
    void testSingleElevatorSingleHumanSimulation() {
        Simulation simulation = Simulation.createSingleElevatorSingleHumanSimulation();
        int stepLimit = 200;

        assertDoesNotThrow(() -> simulation.startAndExecuteUntilDone(stepLimit),
                "Simulation obtained by 'Simulation.createSingleElevatorSingleHumanSimulation()' was aborted because it could not finish in time.");

        simulationFailed = false;
    }

    @Test
    void testSimpleSimulation() {
        Simulation simulation = Simulation.createSimpleSimulation();
        int stepLimit = 500;

        assertDoesNotThrow(() -> simulation.startAndExecuteUntilDone(stepLimit),
                "Simulation obtained by 'Simulation.createSimpleSimulation()' was aborted because it could not finish in time.");
        simulationFailed = false;
    }

    @Test
    void testRandomSimulationSmall() {
        Simulation simulation = Simulation.createRandomSimulation(1, 5, 50, 10);
        int stepLimit = 1_000;

        assertDoesNotThrow(() -> simulation.startAndExecuteUntilDone(stepLimit),
                "Simulation obtained by 'Simulation.createRandomSimulation(1, 5, 50, 10)' was aborted because it could not finish in time.");
        simulationFailed = false;
    }

    @Test
    void testRandomSimulationMedium() {
        Simulation simulation = Simulation.createRandomSimulation(2, 20, 1_000, 50);
        int stepLimit = 10_000;


        assertDoesNotThrow(() -> simulation.startAndExecuteUntilDone(stepLimit),
                "Simulation obtained by 'Simulation.createRandomSimulation(2, 20, 1_000, 50)' was aborted because it could not finish in time.");
        simulationFailed = false;

    }

    @Test
    void testRandomSimulationBig() {
        Simulation simulation = Simulation.createRandomSimulation(3, 100, 100_000, 100);
        int stepLimit = 100_000;
        assertDoesNotThrow(() -> simulation.startAndExecuteUntilDone(stepLimit),
                "Simulation obtained by 'Simulation.createRandomSimulation(3, 100, 100_000, 100)' was aborted because it could not finish in time.");

        simulationFailed = false;
    }
}
