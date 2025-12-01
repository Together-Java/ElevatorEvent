package org.togetherjava.event.elevator;

import java.util.List;
import java.util.stream.LongStream;

import org.togetherjava.event.elevator.enums.HumanState;
import org.togetherjava.event.elevator.models.HumanStatistics;
import org.togetherjava.event.elevator.models.Simulation;
import org.togetherjava.event.elevator.models.View;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SimulationUtils {

  public static final String CANNOT_INSTANTIATE_UTILITY_CLASS = "Cannot instantiate utility class";

  //  public static final String LOG_ELEVATOR_DOESNT_MOVE_NO_PENDING_FLOORS =
  //      "elevator {} doesn't move, no pending floors";

  public static final String LOG_ELEVATOR_MOVING_FROM_TO =
      "elevator {} moving {} from {} to {} (target: {})";

  private SimulationUtils() {
    throw new UnsupportedOperationException(CANNOT_INSTANTIATE_UTILITY_CLASS);
  }

  public static void printSummary(Simulation simulation) {
    View view = simulation.getView();
    view.printSummary();
  }

  public static void prettyPrint(Simulation simulation) {
    View view = simulation.getView();
    view.prettyPrint();
  }

  public static void printResult(Simulation simulation) {
    log.info("Steps: {}", simulation.getStepCount());
    log.info("Median time spend per state:");

    for (HumanState state : HumanState.values()) {
      int averagePercentage = getAverageTimePercentageSpendForState(simulation, state);
      log.info("\t{}: {}%", state, averagePercentage);
    }
  }

  public static int getAverageTimePercentageSpendForState(Simulation simulation, HumanState state) {

    List<HumanStatistics> humanStatistics = simulation.getHumanStatistics();

    long stepCount = simulation.getStepCount();

    if (stepCount == 0) {
      return 0;
    }

    LongStream sortedSteps =
        humanStatistics.stream().mapToLong(stats -> stats.stepsForState(state)).sorted();

    long medianSteps =
        humanStatistics.size() % 2 == 0
            ? (long)
                sortedSteps.skip(humanStatistics.size() / 2 - 1).limit(2).average().orElseThrow()
            : sortedSteps.skip(humanStatistics.size() / 2).findFirst().orElseThrow();

    long medianPercentage = 100 * medianSteps / stepCount;
    return (int) medianPercentage;
  }
}
