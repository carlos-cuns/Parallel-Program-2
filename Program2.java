import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Program2
{
    final static int totalGuests = 10;
    public static void main(String args[]) throws InterruptedException
    {
        // Problem 1
        Labyrinth lab = new Labyrinth(totalGuests);
        ArrayList<Thread> threads = new ArrayList<>();

        // first guest is the announcer
        System.out.println("=================================================================================");
        System.out.println("\t\t\t\tBefore Game:\n");
        System.out.println("[Announcer]: \"I will keep track of how many cupcakes are handed out!\"");
        System.out.println("\t     \"Only eat a cupcake if you have never had one.\"");
        System.out.println("=================================================================================");
        System.out.println("\t\t   Game Start - Cupcake #1 is on the plate.");

        for (int i = 1; i < totalGuests; i++)
        {
            Thread t = new Thread(new Guest(lab, false));
            t.start();
            threads.add(t);
        }
        Thread announcer = new Thread(new Guest(lab, true));
        announcer.start();
        threads.add(announcer);
        
        for(Thread t : threads)
        {
            t.join();
        } 

        // Problem 2
        System.out.println("\t\t\t    Crystal Vase Exposition\n");
        System.out.println("[Announcer]: \"The Minotaur would like everyone to see his Crystal Vase.\"");
        System.out.println("\t     \"We have decided that a line (queue) is the best way to do so.\"");
        System.out.println("=================================================================================");
        System.out.println("\t\t\t    Queue and Showroom\n");

        Showroom showroom = new Showroom();
        VaseGuest[] Qthreads = new VaseGuest[totalGuests];
        // Create five guests
        for (int i = 0; i < totalGuests; i++)
        {
            Qthreads[i] = new VaseGuest(i + 1, showroom);
            Qthreads[i].start();
        }

        for (VaseGuest t : Qthreads) {
            t.join();
        }
        System.out.println("=================================================================================");

    }
}

class Guest implements Runnable {
    private final Labyrinth lab;
    private final boolean announcer;
    private boolean ate = false;

    public Guest(Labyrinth lab, boolean announcer) {
        this.lab = lab;
        this.announcer = announcer;
    }

    public void run() {
        while (!lab.allEaten()) {
            boolean eat = lab.enterLabyrinth(announcer, ate);
            if (eat)
            {
                // only set to true when a guest eatis first cupcake
                ate = true;
                System.out.println("\n[Guest]: eats a cupcake");
            }
        }
        if(announcer)
        {
            ate = true;
            System.out.println("[Announcer]: \"I will eat this last cupcake.\"");
        System.out.println("=================================================================================");
        System.out.println("\t\t\t\tTo End Game:\n");
        System.out.println("[Announcer] to Minotaur: \"All guests have eaten and therefore visited!\"");
        System.out.println("=================================================================================");
        }
    }
}

class Labyrinth
{
    // start at one since the announcer is assumed to count him/herself
    private AtomicInteger cupcakesHandedOut = new AtomicInteger(1);
    private static boolean cupcake = true;
    private final int numGuests;
    private Lock lock = new ReentrantLock();
    
    public Labyrinth(int numGuests)
    {
        this.numGuests = numGuests;
    }

    public boolean allEaten()
    {
        //System.err.println("numEmpy: "+ cupcakesHandedOut.get());
        return cupcakesHandedOut.get() == numGuests;
    }

    public boolean enterLabyrinth(boolean announcer, boolean ate) {
        lock.lock(); 
        boolean eat = false;
        try {
            if (announcer) {
                if (!cupcake) {
                    // if the plate is empty we know that a new guest has gone through
                    cupcakesHandedOut.incrementAndGet();
                    System.out.println("\n[Announcer]: Placing cupcake #"+ cupcakesHandedOut.get());
                    cupcake = true;
                }
            } else {
                if (!ate) {
                    // guest eats the cupcake on the first time through
                    if(cupcake)
                    {
                        cupcake = false;
                        eat = true;
                    }
                }
            }
        } finally {
            lock.unlock();
        }
        return eat;
    }
}

class VaseGuest extends Thread {
    public final int num;
    private final Showroom showroom;

    public VaseGuest(int num, Showroom showroom) {
        this.num = num;
        this.showroom = showroom;
    }

    @Override
    public void run() {
        showroom.enterQueue(this);
        showroom.enterShowroom();
    }
}

class Showroom {
    private final ConcurrentLinkedQueue<VaseGuest> guestQueue = new ConcurrentLinkedQueue<>();
    private VaseGuest currentG;
    private Lock lock = new ReentrantLock();

    public void enterQueue(VaseGuest g) {
        guestQueue.offer(g);
    }

    public void enterShowroom() 
    {
        lock.lock();
        VaseGuest g = guestQueue.poll();
        if (g != null) {
            String display = "Guest " + g.num + " entered the showroom";
            if(currentG != null)
            {
                display += " after Guest " + currentG.num + " notified them";
            }
            System.out.println(display + ".");
            currentG = g;
        }
        lock.unlock();
    }
}

