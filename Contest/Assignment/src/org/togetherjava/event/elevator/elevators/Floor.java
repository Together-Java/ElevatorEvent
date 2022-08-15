package org.togetherjava.event.elevator.elevators;

import org.togetherjava.event.elevator.humans.ElevatorListener;
import org.togetherjava.event.elevator.humans.Passenger;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that holds data for each floor, package-private as technically all this is considered
 * implementation detail.
 */
class Floor {
    private final int number;
    /**
     * Passengers that currently wait for an elevator, should not include passengers that have arrived
     * at their destination.
     */
    private final Collection<Passenger> passengers = ConcurrentHashMap.newKeySet();
    /**
     * Elevators currently stopping at this floor.
     */
    private final Collection<Elevator> elevators = ConcurrentHashMap.newKeySet();

    Floor(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Floor.class.getSimpleName() + "[", "]")
                .add("number=" + number)
                .toString();
    }

    @Override
    public int hashCode() {
        return number;
    }

    void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }

    void removePassenger(Passenger passenger) {
        passengers.remove(passenger);
    }

    void addElevator(Elevator elevator) {
        elevators.add(elevator);
    }

    void removeElevator(Elevator elevator) {
        elevators.remove(elevator);
    }

    synchronized int getActivePassengersCount() {
        return passengers.size() + elevators.stream().map(e -> e.getPassengers().size()).reduce(0, Integer::sum);
    }


    /**
     * Notify all passengers of all elevators on this floor that they may exit the elevator if they wish.
     */
    synchronized void fireElevatorPassengerEvents() {
        for (Elevator elevator : elevators) {
            for (ElevatorListener passenger : elevator.getPassengers()) {
                passenger.onElevatorArrivedAtFloor(elevator);
            }
        }
    }

    /**
     * Notify all passengers on this floor that they may enter an elevator if they wish.
     */
    synchronized void fireElevatorArrivalEvents() {
        for (Passenger passenger : passengers) {
            for (Elevator elevator : elevators) {
                passenger.onElevatorArrivedAtFloor(elevator);
            }
        }
    }

    /**
     * Notify all passengers on this floor that they may request an elevator if they wish.
     */
    synchronized void fireElevatorRequestEvents(FloorPanelSystem floorPanelSystem) {
        for (Passenger passenger : passengers) {
            passenger.onElevatorSystemReady(floorPanelSystem);
        }
    }
}
