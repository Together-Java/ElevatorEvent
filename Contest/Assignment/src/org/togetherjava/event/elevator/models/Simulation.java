package org.togetherjava.event.elevator.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Simulation {
  private final List<Human> humans;
  private final List<Elevator> elevators;
  @Getter private final ElevatorSystem elevatorSystem;
  @Getter private final View view;
  @Getter private long stepCount;
  @Getter private final List<HumanStatistics> humanStatistics;

  public Simulation(List<Elevator> elevators, List<Human> humans) {
    log.info("Creating new Simulation");
    this.elevators = new ArrayList<>(elevators);
    this.humans = new ArrayList<>(humans);

    this.elevatorSystem = new ElevatorSystem();
    this.elevators.forEach(this.elevatorSystem::registerElevator);
    this.humans.forEach(this.elevatorSystem::registerElevatorListener);

    this.humanStatistics = this.humans.stream().map(HumanStatistics::new).toList();
    this.view = new View(this);
  }

  public List<Human> getHumans() {
    return Collections.unmodifiableList(this.humans);
  }

  public List<Elevator> getElevators() {
    return Collections.unmodifiableList(this.elevators);
  }

  public void increaseStepCount() {
    this.stepCount++;
  }
}
