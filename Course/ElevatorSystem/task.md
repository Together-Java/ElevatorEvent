In this section, we will focus on creating the last core piece, the
[ElevatorSystem.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/elevators/ElevatorSystem.java) class.

This class will know all elevators and humans,
receiving requests from humans and sending them to the elevators,
as well as sending events from elevators to humans.

## Interfaces

Before we get started with the actual class, we have to think about the
interfaces between elevators and humans.

For the communication, we will expose humans as `ElevatorListener` to elevators.
Further, elevators will be exposed as `ElevatorPanel` to humans and the elevator system
itself will be exposed as `FloorPanelSystem` to humans.

These interfaces will represent the actions that are actually needed to create the
desired interaction.

### `ElevatorListener`

Head over to [ElevatorListener.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/humans/ElevatorListener.java).

This interface represents all the actions that a human will be interested in.

Let [Human.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/humans/Human.java)
implement this interface. The actual code for those two methods can still be left unimplemented,
we will take care of that later.

### `ElevatorPanel`

Head over to [ElevatorPanel.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/elevators/ElevatorPanel.java).

This interface represents all the actions that an elevator will want to expose to humans.

Let [Elevator.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/elevators/Elevator.java)
implement this interface.

You actually already wrote the code for two of those methods. The code for the third,
`requestDestinationFloor` can again be left unimplemented for now. 

### `FloorPanelSystem`

Last but not least, head over to [FloorPanelSystem.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/elevators/FloorPanelSystem.java).

This interface represents the action that the elevator system itself exposes to humans
standing on the corridor.

Let [ElevatorSystem.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/elevators/ElevatorSystem.java)
implement this interface.

Leave the method `requestElevator` empty for now. This will be the focus for the
second part of this event. 


## Putting it together

Now, all classes should be ready for [ElevatorSystem.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/elevators/ElevatorSystem.java)
to wire them up.

The class has two methods to register elevators and humans (as `ElevatorListener`) respectively.
Implement those methods by simply keeping track of them, for example put them into a data-structure
which you may add as field.

Afterwards, you have to implement the `ready()` method. This method is supposed to be called
after everything has been registered and its job is to fire the _Elevator-System-Ready_ event
to all previously registered `ElevatorListener`s.

I.e. invoke `listener.onElevatorSystemReady(...)` on all of them.

## Preparing for movement

The last thing we need before we can put the whole system alive is a way to command movement,
as well as the `onElevatorArrivedAtFloor` event.

For this, we will add a `public void moveOneFloor()` method to [Elevator.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/elevators/Elevator.java).
You can keep the method empty for now, this will be a focus of the contest.

This method will now be triggered from [ElevatorSystem.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/elevators/ElevatorSystem.java)
in a method with the same name. The systems `moveOneFloor` method is supposed to
invoke `moveOneFloor` on all its registered elevators. And then invoke `onElevatorArrivedAtFloor`
on all registered humans (`ElevatorListener`).

That way, humans will later get the chance to enter and exit elevators.

## Testing

The class is now ready, go ahead and try it out. Play with it in
[Main.java](course://Course/ElevatorSystem/src/org/togetherjava/event/elevator/Main.java).

For example, you may write:

```java
ElevatorSystem system = new ElevatorSystem();

system.registerElevator(new Elevator(3, 4, 6));

system.registerElevatorListener(new Human(1, 4));
system.registerElevatorListener(new Human(6, 3));

system.ready();

system.moveOneFloor();
```

Verify that the methods are invoked correctly and that `ready()`
fired the event to the two humans, notifying both.

Also, `moveOneFloor` should have triggered `onElevatorArrivedAtFloor` for humans.

At this point, if you have done everything right, all tests should pass, and you
can wrap up the actual course, moving on to the contest 👍
