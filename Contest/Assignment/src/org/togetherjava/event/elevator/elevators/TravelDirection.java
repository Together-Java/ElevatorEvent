package org.togetherjava.event.elevator.elevators;

public enum TravelDirection {
    UP,
    DOWN;

    public static TravelDirection getTravelDirection(int startingFloor, int destinationFloor) {
        if (startingFloor > destinationFloor) {
            return DOWN;
        } else if (startingFloor < destinationFloor) {
            return UP;
        } else {
            return null;
        }
    }
}
