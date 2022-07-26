package org.togetherjava.event.elevator;

import org.togetherjava.event.elevator.elevators.Elevator;
import org.togetherjava.event.elevator.humans.Human;

public final class Main {
    /**
     * Starts the application.
     * <p>
     * Will create an elevator-system simulation, execute it until it is done
     * and pretty print the state to console.
     *
     * @param args Not supported
     */
    public static void main(final String[] args) {
        Human human = new Human(1, 4);

        System.out.println(human.getCurrentState());
        System.out.println(human.getStartingFloor());
        System.out.println(human);

        Elevator elevator = new Elevator(3, 4, 6);

        System.out.println(elevator.getId());
        System.out.println(elevator.getFloorsServed());
        System.out.println(elevator);
        System.out.println("Hello World!");
    }
}
