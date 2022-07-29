The task is divided into two sections. One, in which the project will be explained to you,
and you have to write most of the basic code with assistance. And second, in which you will have
to finish the code on your own and solve the task.

## Elevator Test

The project mainly consists of an elevator system, controlling multiple elevators of a building,
and a set of humans in that building, starting at given floors, who want to use the
elevators in order to reach destination floors.

### What is provided

We provide you with the rough layout and guide you through creating most of the basic code.
The system can then be executed step by step as simulation.

#### Visualization

Also, we created a visualization of the system to aid you in debugging your solution.
When you run the code, it will print an ASCII representation of the building,
its humans and elevators, in each step. Here is an example:

![building example](https://i.imgur.com/sukCTyX.png)

You see a building with 10 floors, viewed from the side. On the left, there are the corridors
annotated with the amount of people currently waiting (**W**) for an elevator to arrive, and the
amount of people who already arrived at their destination (**A**).

The labels on the very left denote the current floor (**1 to 10**).

To the right of the corridors are the elevator shafts with each elevator respectively (**5 in total**).
Each elevator is annotated with the amount of people currently inside it (in the middle),
as well as the unique ID of the elevator (at the bottom).

#### Simulations

To make it easy for you to debug your code, we also provide you with pre-created simulation
setups in the `main` method, which you can select from, and fine-tune as you wish:

```java
// Select a desired simulation for trying out your code.
// Start with the simple simulations first, try out the bigger systems once you got it working.
// Eventually try out the randomly generated systems. If you want to debug a problem you encountered
// with one of them, note down the seed that it prints at the beginning and then use the variant that takes this seed.
// That way, it will generate the same system again, and you can repeat the test.

Simulation simulation = Simulation.createSingleElevatorSingleHumanSimulation();
// Simulation simulation = Simulation.createSimpleSimulation();
// Simulation simulation = Simulation.createRandomSimulation(5, 50, 10);
// Simulation simulation = Simulation.createRandomSimulation(putDesiredSeedHere, 5, 50, 10);
```

### Goal

#### Termination

First and foremost, the main objective of the task is to ensure that everyone arrives at their destination.
As simple as this sounds, this can already be a tough challenge in itself.

To make your life easy, we abort your code if it seems to be stuck, and you will see an error message:

```java
if (stepCount >= 100_000) {
    throw new IllegalStateException("Simulation aborted. All humans should have arrived"
            + " by now, but they did not. There is likely a bug in your code.");
}
```

#### Optimization

Once you created a solution that actually successfully terminates, you can start optimizing your algorithm.
That is, humans should ideally arrive as fast as possible, and, on average,
spend as little time as possible waiting.

Once your code terminates, we will provide you with useful statistics:

```
Simulation is done.
Steps: 26
Median time spend per state:
	IDLE: 0%
	WAITING_FOR_ELEVATOR: 2%
	TRAVELING_WITH_ELEVATOR: 30%
	ARRIVED: 67%
```

The most important metric here are the values for `WAITING_FOR_ELEVATOR` and
`TRAVELING_WITH_ELEVATOR`, which should be as low as possible. Ideally, the total step count is
also minimized. But be aware, that it can be a thing that a single human might take long to arrive,
while everyone else arrives fast. Because of that, we measure the median and not the average. 

In general, it is not possible to find an optimal solution, a compromise is needed.
We do not have any hard numbers that you must pass here. Try to come up with something that
sounds good and fair in your opinion, and just have fun playing around.

#### Polish

When you are happy with the codes' performance, you can start to polish it.
Try to make the code as readable as possible.

Look out for good variable names, comments, JavaDoc, helper methods, error detection, argument
validation and whatever else comes to your mind when thinking about clean code.

In particular when it comes to the grading, expect that all submissions work and have at least a
mediocre performance in terms of minimizing the waiting times for humans already.
That means that the degree of how clean your code is, will make an important impact and will
most likely be the driving factor if you want to win this contest! 💰
