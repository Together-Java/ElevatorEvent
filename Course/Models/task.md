Your first task will be to create the basic models for this project. Make sure to write your code inside the
designated placeholder boxes, otherwise your code will not be saved when you progress to the next section.

We will need a [Human.java](course://Course/Models/src/org/togetherjava/event/elevator/humans/Human.java)
file, that represents the individual humans who want to use elevators to travel around.

And an [Elevator.java](course://Course/Models/src/org/togetherjava/event/elevator/elevators/Elevator.java)
file, which will transport humans along the floors.

## Human

We start with the [Human.java](course://Course/Models/src/org/togetherjava/event/elevator/humans/Human.java).

### State

Before we begin with the class itself, we need an enum that represents the `State` of a human.
Humans are supposed to be in either of the following states:

* `IDLE`
* `WAITING_FOR_ELEVATOR`
* `TRAVELING_WITH_ELEVATOR`
* `ARRIVED`

Please adjust the given enum accordingly to have those 4 states.

Also, a human will need to keep track of their `currentState`.
Please add such a **field** to the class. The `getCurrentState()` method is supposed to return this field.
The field itself can be kept on the `IDLE` state for now, but will eventually be able to change.

### Floors

Next up, humans need a `startingFloor` and a `destinationFloor`.
For this project, we will assume that the first floor in a building is floor `1` (and not `0`).

The human is supposed to take in those two values via constructor and remember them in fields,
from where it can then serve them to users with `getStartingFloor()` and `getDestinationFloor()`.

### Elevator ID

A human needs to be able to keep track of the elevator they are currently in, if any.
Elevators have unique IDs and the `getCurrentEnteredElevatorID()` method is supposed to return
that respectively, or an empty optional if the human is on the corridor.

The logic for this method could be backed by a field, it is your choice how to implement it.

For now, we will not change the value of this yet though and the method can just
`return OptionalInt.empty()`.

### `toString`

To ease debugging, it is highly recommended that you add a `toString` method to your `Human` class.

## Elevator

The next class to focus on is [Elevator.java](course://Course/Models/src/org/togetherjava/event/elevator/elevators/Elevator.java).

Similar to `Human`, you will have to add a couple of fields, set up by the constructor.

### Floors

An elevator has a range of floors that it can serve. The range is defined by the
`minFloor` (at least greater than or equals to `1`), ranging up to the last supported floor, given by
`minFloor + floorsServed - 1`.

In might sound complex at first, but is actually simple once we take a look at some examples.
Suppose an elevator with:

* `minFloor = 3`
* `floorsServed = 4`

This elevator supports floors `3, 4, 5, 6`. So the range starts at `3` and the total
amount of floors served is `4`, making it end at floor `6`. We assume no gaps in this range,
elevators support all floors on their range.

On top of that, an elevator also has a starting position, indicated by `currentFloor`.

Implement the constructor and the corresponding methods.

### ID

Each elevator needs a unique ID. This ID is supposed to be returned by `getId()`.

Not only will this be useful for debugging later on, but this ID will also be displayed later
in the visualization. For the best experience, it should be positive and start small.
For example, `0`, `1`, `2` and so on.

How you implement this, is up to you.

### `toString`

Likewise to `Human`, it is highly recommended that you add a `toString` method to your `Elevator` class.

## Test

Latest at this point, you should actually head over to [Main.java](course://Course/Models/src/org/togetherjava/event/elevator/Main.java)
and try out your classes. See if everything works as expected, play around with it.
For example, write something like:

```java
Human human = new Human(1, 4);

System.out.println(human.getCurrentState());
System.out.println(human.getStartingFloor());
System.out.println(human);

Elevator elevator = new Elevator(3, 4, 6);

System.out.println(elevator.getId());
System.out.println(elevator.getFloorsServed());
System.out.println(elevator);
```

At this point, if you have done everything right, all tests should pass, and you can move on to
the next section 👍

If you have put your implementations into the highlighted placeholder-areas,
your code should carry over automatically.
