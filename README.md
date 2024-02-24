# Minotaur's Birthday Party

## To compile and run this program use:

```
javac Program2.java
java Program 2
```

In the main function you can set the totalGuest to the number of guests invited.

## Problem 1

### Strategy

Before the game, all of the guests come up with a solution with the following properties

- Every one is allowed one cupcake.
- One person (the announcer) is in charge of asking the servants to place a new cupcake
- That same person is in charge of counting the amount of times they make a request to the servants.
- Once the number of requests is equal to the number of guests - 1 (The announcer ate the first cupcake), they will announce to the minotaur that every guest has entered the labyrinth at least once.

This works because for every guest to have eaten one cupcake, they would have to enter the labyrinth.

### Efficiency and Correctness

My efficiency is related to the fact that the guests will have to enter several times until the announcer counts N cupcakes placed. This introduces some variance in results but makes sure that the guests have some way to inform the Minotaur about everyone's completion of the Labyrinth.

My correctness is evident when seeing the visuals provided by the output.

- Every guest is shown consuming one single cupcake
- The announcer is shown to place N - 1 cupcakes since the first is placed by the Minotaur

Using the notes, I knew that I should use a lock for any actions that had to be mutually exclusive.

### Experimentation

The most difficult part of coming up with a solution was trying to figure out how any of the guests could keep track of the others with out everyone communicating (Having some sort of global tracker that everyone can access). I solved this by having the announcer have more permissions than the rest and having only them keep track of everything through a personal global counter.

## Problem 2

### Strategy

To keep maximum order and a sure method that everyone will see the vase, the guests came up with following strategy 3.

### Advantages / Disadvantages

- Open Door Strategy:
  - Advantages:
    - Anyone can check if the room is in use.
  - Disadvantages:
    - Since anyone can check, a crowd can form which can cause blockage in the party.
- Sign Strategy:
  - Advantages:
    - Sign let's the whole party see without everyone crowding the door.
  - Disadvantages:
    - Guests have to make sure to change the sign when entering and exiting.
    - Many people can read the AVAILABLE and run to enter at the same time, this could also cause crowding (door blocked)
- Queue Strategy:
  - Advantages:
    - Everyone can line up in an ordered line: no crowding.
    - Since there is a line, it is sure that everyone can see the Crystal Vase.
  - Disadvantages:
    - Every guest has to notify the next, which could be forgotten and everyone is waiting on an empty room.
    - A line means long wait times for the people at the end (Last in Last out).

### Efficiency and Correctness

The efficiency is that everyone is able to join the queue quickly and then they are processed in order. This means that there is no confusion between guests (Threads). The current guest is stored (put into room) and then when they exit they can notify the next guest to enter (store and pop).

The correctness is visible in the visuals provided by the outputed statement:
every guest is shown to enter the showroom and then notify the next guest when exiting

### Experimentation

The main problem to solve here was to make sure only one person entered the showroom at once. I figured that I could use a lock when a thread entered the room and then unlock it when they exited. The queue was solved by implementing a concurrent queue data structure.
