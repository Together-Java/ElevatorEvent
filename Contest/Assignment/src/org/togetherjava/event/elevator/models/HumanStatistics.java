package org.togetherjava.event.elevator.models;

import java.util.EnumMap;
import java.util.Map;

import org.togetherjava.event.elevator.enums.HumanState;

public final class HumanStatistics {
  private final Human human;
  private final Map<HumanState, Long> stateToStepCount = new EnumMap<>(HumanState.class);

  HumanStatistics(Human human) {
    this.human = human;
  }

  public void step() {
    HumanState state = this.human.getCurrentState();
    this.stateToStepCount.put(state, this.stateToStepCount.getOrDefault(state, 0L) + 1);
  }

  public long stepsForState(HumanState state) {
    return this.stateToStepCount.getOrDefault(state, 0L);
  }
}
