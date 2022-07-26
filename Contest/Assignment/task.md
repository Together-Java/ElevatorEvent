Welcome to the actual contest 🙌

Before you get started, take your time to familiarize
with the code base and what we changed.

## Changes

### Base

First, to give everyone the same starting point, we replaced the code
you have previously written with the reference solution.

### Simulation

We have added a new package with a few new files:

* [Simulation.java](course://Contest/Assignment/src/org/togetherjava/event/elevator/simulation/Simulation.java)
* [View.java](course://Contest/Assignment/src/org/togetherjava/event/elevator/simulation/View.java)
* [HumanStatistics.java](course://Contest/Assignment/src/org/togetherjava/event/elevator/simulation/HumanStatistics.java)

Their purpose is to assist you in debugging while you develop your solution.
You can, but certainly do not have to take a look at these files.

### Main

Also, we have adjusted the [Main.java](course://Contest/Assignment/src/org/togetherjava/event/elevator/Main.java)
file. It is set up to run the elevator simulation.

Here, you can select which simulation you want to run. Feel free to adjust this to your needs.

## Assignment

As for the actual task, you have to implement the remaining **5 TODOs**:

* [ElevatorSystem.java](course://Contest/Assignment/src/org/togetherjava/event/elevator/elevators/ElevatorSystem.java)
  * `requestElevator` - select the best elevator and let it move to the human for pickup
* [Elevator.java](course://Contest/Assignment/src/org/togetherjava/event/elevator/elevators/Elevator.java)
  * `requestDestinationFloor` - Memorize the floor request, for example using certain data structures
  * `moveOneFloor` - move the elevator one floor according to the current memorized requests
* [Human.java](course://Contest/Assignment/src/org/togetherjava/event/elevator/humans/Human.java)
  * `onElevatorSystemReady` - send the request for pickup
  * `onElevatorArrivedAtFloor` - either enter or exit the elevator

How you implement them is totally up to you. In particular, for the best elevator selection and
the elevator request memorization, you can get very creative. Ultimately, the goal is not only to create
a working solution, but also a solution that is fair and brings humans to their destination fast, on average.

### Edge case

You may assume that all elevators can serve the whole building. We will not test your code with setups where
there are unreachable floors, or where an elevator can not serve a particular floor.

It is safe for a human to enter any elevator, the elevator can eventually reach the desired floor directly.

However, you are welcome to optionally support situations in which humans must travel with multiple
elevators to reach their destination.

### Frame

You are welcome to change the existing code to your needs. But please keep the modifications reasonable.

For example, you may and certainly will have to add some extra fields here and there,
and maybe adjust a constructor and similar, as well as adding new helper methods - maybe even a helper class.

But you should not alter the structure of existing classes heavily or change the overall system.
Also, the automated tests will most likely fail otherwise.

### Goal

#### Termination

First and foremost, the main objective of the assignment is to ensure that everyone arrives at their destination.
As simple as this sounds, this can already be a tough challenge on itself.

To make your life easy, we abort your code in such a case, and you will see an error message:

```java
if (stepCount >= 100_000) {
    throw new IllegalStateException("Simulation aborted. All humans should have arrived"
            + " by now, but they did not. There is likely a bug in your code.");
}
```

Finishing this will make you eligible for the prize giveaway.

If you struggle, you can try to implement a trivial solution instead.
Check out the [Paternoster lift](https://en.wikipedia.org/wiki/Paternoster_lift).
This is reasonably easy to implement and everyone will eventually arrive.

By no means is this a good implementation in terms of reducing the arrival time or
being fair for humans, but it gets the job done.

##### Tests

We have added automated tests to help you debug and find issues.
Once they all pass, your code is also most likely correct. All the tests are visible to you and
can also be executed manually, if needed:

![run tests manually](https://i.imgur.com/cmAHbSm.png)

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

#### Bored? Bonus tasks

In case you blazed through this assignment easily and are looking for a few additional things to do,
here is a list of stuff that you can optionally implement or think about:

* Account for different floor ranges among elevators, such that humans may have to take
  multiple elevators to reach their destination.
* Support that humans enter the simulation while it is already running.
* Add that humans can report state changes via a listener system, let Simulation listen to the arrived state 
  to massively speed up its `isDone()` check, which currently iterates all humans each step.
* Figure out why the simulation is taking so long (about 20 seconds for `Simulation.createRandomSimulation(3, 100, 100_000, 100)`)
  it is due to a single particular line in the code. If this line is improved (not trivial),
  this can be reduced down to about 3 seconds.
* Make the code thread-safe, start by calling `moveOneFloor()` and `onElevatorArrivedAtFloor`
  in parallel on the elevators and humans. Things will start to break now, unless the code is made thread-safe.
* Next to your existing elevators, create a [Paternoster lift](https://en.wikipedia.org/wiki/Paternoster_lift)
  and compare the performance. Try to also support a building with mixed elevator types.
* Create a JavaFX or Swing GUI for the visualization, replacing the ASCII `View.java`.

## How to submit

The event runs for one week. Until then, please submit your solution per DM to any of the
moderators of Together Java.

The easiest way to share your results is by just zipping the whole project folder. Alternatively,
you can also just send us the source code for the files you changed. At minimum, this would be:

* `ElevatorSystem.java`
* `Elevator.java`
* `Human.java`

We will accept any format, as long as it is clear what you have changed in the source code.

## Need help

If you have trouble solving the tasks, or something is just unclear to you,
feel free to openly ask in the server.

Besides being a contest, this is also meant as learning experience.
So we will definitely provide you with assistance 👍

Enjoy and happy coding 🙌
