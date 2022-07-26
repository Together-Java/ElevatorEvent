package org.togetherjava.event.elevator.simulation;

import org.togetherjava.event.elevator.humans.Human;

import java.util.EnumMap;
import java.util.Map;

final class HumanStatistics {
    private final Human human;
    private final Map<Human.State, Long> stateToStepCount = new EnumMap<>(Human.State.class);

    HumanStatistics(Human human) {
        this.human = human;
    }

    void step() {
        Human.State state = human.getCurrentState();
        stateToStepCount.put(state, stateToStepCount.getOrDefault(state, 0L) + 1);
    }

    long stepsForState(Human.State state) {
        return stateToStepCount.getOrDefault(state, 0L);
    }
}
