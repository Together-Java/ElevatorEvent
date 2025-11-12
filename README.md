![title](https://i.imgur.com/Of1ua1J.png)

Welcome to the **Elevator Event** hosted by **Together Java**!

## What is this?

This event consists of a guided course, in which you will create an elevator system that
transports humans in a building from floor to floor, as well as an actual contest where you
are required to finish the implementation, try to improve your solution to reduce the time humans
have to wait, and finally polish your submission.

The event is **ideal for beginners** who feel confident with writing
small applications already. In fact, this task is inspired by a commonly executed application test for
Junior developers at companies, called the _Elevator Test_.

## Prizes

### Jetbrains licenses

Among all working submissions (elevators have to eventually transport humans to their destinations),
we will **randomly** give away up to **2 annual licenses** for any [**JetBrains product**](https://www.jetbrains.com/products/).

![JetBrains logo](https://i.imgur.com/h6jXoNY.png)

This will get you access to any of their 11 IDEs, 3 extensions and 2 profilers.

![all products pack](https://i.imgur.com/IL4dDs0.png)

### Role

Additionally, submissions will be graded and the top users are awarded with
the fancy '_Ruler of Elevators_' role and a shiny badge!

![role and badge](https://i.imgur.com/Uvg0WlA.png)

## How to get started

The event comes as a zip-file in form of a **JetBrains Academy course**,
which is a plugin for IntelliJ.

To participate in this event, you need:

* [IntelliJ IDEA](https://www.jetbrains.com/idea/download) (the free Community Edition is more than enough)
* [JetBrains Academy plugin](https://plugins.jetbrains.com/plugin/10081-jetbrains-academy)
* [Java 25](https://adoptium.net/) (or newer)
* Download the event from [Elevator_Event_Together_Java.zip](https://github.com/Together-Java/ElevatorEvent/releases)

There is also a special version of IntelliJ that comes with JetBrains Academy
pre-installed already, [IntelliJ IDEA Edu](https://www.jetbrains.com/edu-products/download/?section=idea).

Once you got that, import the zip-file as course into JetBrains Academy:

![Start course](https://i.imgur.com/nek0xjS.png)
![Open course from disk](https://i.imgur.com/NpBmz8u.png)
![Select course zip](https://i.imgur.com/DoWwBl3.png)

Make sure to select Java 25 and hit the **Start** button. 

![Select Java 25 and start](https://i.imgur.com/HDiuNwc.png)

IntelliJ now opens the event, it will look something like:

![IDE](https://i.imgur.com/xJ906mT.png)

On the right side you have the explanation that will guide you through the event.
Together with buttons that allow you to check your code and advance to the next section.

In the center is the code editor, where you will edit your solution.

On the left side is the overview with all sections and files contained in the event.

## How to submit

Please submit your solution before the event ends per DM to any of the
moderators of Together Java.

The easiest way to share your results is by just zipping the whole project folder.

We will accept any format, as long as it is clear what you have changed in the source code.

While the event is still ongoing, please do not promote your solution openly in the server
though to not ruin the event for others.

## Need help

If you have trouble solving the tasks, or something is just unclear to you,
feel free to openly ask in the server.

Besides being a contest, this is also meant as learning experience.
So we will definitely provide you with assistance 👍

Enjoy and happy coding 🙌

### Known issues

#### Gradle uses wrong JVM

In case you encounter an error message, such as:

![Cause: error: invalid source release: 25](https://i.imgur.com/xrT39xi.png)

Go to your Gradle settings (you can find them on the right, or directly in Intellij Settings):

![gradle settings](https://i.imgur.com/7ECw8Jw.png)

and select a JDK 25 instead of `#JAVA_INTERNAL`:

![select version](https://i.imgur.com/h1FFXYn.png)
