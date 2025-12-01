package org.togetherjava.event.elevator.humans;

@FunctionalInterface
public interface HumanArrivedListener {
    void onHumanArrived(Human human);
}

