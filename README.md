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

### Jetbrains All Product Pack

Among all working submissions (elevators have to eventually transport humans to their destinations),
we will randomly give away a month license for the [**All Products Pack**](https://www.jetbrains.com/all/)
by **JetBrains**, sponsored by **Taz** (thanks 🎉).

![Taz#1072](https://i.imgur.com/N4cJdVF.png)

This will get you access to **all JetBrains desktop tools** including 11 IDEs, 3 extensions and 2 profilers.

![all products pack](https://i.imgur.com/IL4dDs0.png)

### Roles

Additionally, submissions will be graded and the top users are awarded with
a fancy role and a shiny badge!

![role and badge](https://i.imgur.com/Uvg0WlA.png)

## How to get started

The event comes as a zip-file in form of a **EduTools course**,
which is a plugin for IntelliJ.

To participate in this event, you need:

* [IntelliJ IDEA](https://www.jetbrains.com/idea/download) (the free Community Edition is more than enough)
* [EduTools plugin](https://plugins.jetbrains.com/plugin/10081-edutools)
* [Java 17](https://adoptium.net/) (or newer)
* Download the event from [ElevatorEventTogetherJava.zip](https://github.com/Together-Java/ElevatorEvent/releases)

There is also a special version of IntelliJ that comes with EduTools
pre-installed already, [IntelliJ IDEA Edu](https://www.jetbrains.com/edu-products/download/#section=idea).

Once you got that, import the zip-file as course into EduTools:

![Start course](https://i.imgur.com/xc4BLJI.png)
![Open course from disk](https://i.imgur.com/uHurpw1.png)
![Select course zip](https://i.imgur.com/DoWwBl3.png)

Make sure to select Java 17 and hit the **Start** button. 

![Select Java 17 and start](https://i.imgur.com/yWM37Oo.png)

IntelliJ now opens the event, it will look something like:

![IDE](https://i.imgur.com/xJ906mT.png)

On the right side you have the explanation that will guide you through the event.
Together with buttons that allow you to check your code and advance to the next section.

In the center is the code editor, where you will edit your solution.

On the left side is the overview with all sections and files contained in the event.

## How to submit

The event runs for two weeks. Until then, please submit your solution per DM to any of the
moderators of Together Java.

The easiest way to share your results is by just zipping the whole project folder.
We will accept any format, as long as it is clear what you have changed in the source code.

## Need help

If you have trouble solving the tasks, or something is just unclear to you,
feel free to openly ask in the server.

Besides being a contest, this is also meant as learning experience.
So we will definitely provide you with assistance 👍

Enjoy and happy coding 🙌

### Known issues

#### Gradle uses wrong JVM

In case you encounter an error message, such as:

![Cause: error: invalid source release: 17](https://i.imgur.com/xrT39xi.png)

Go to your Gradle settings (you can find them on the right, or directly in Intellij Settings):

![gradle settings](https://i.imgur.com/7ECw8Jw.png)

and select a JDK 17 instead of `#JAVA_INTERNAL`:

![select version](https://i.imgur.com/h1FFXYn.png)
