import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Program2
{
    final static int totalGuests = 10;
    public static void main(String args[]) throws InterruptedException
    {
        Labyrinth lab = new Labyrinth(totalGuests);
        ArrayList<Thread> threads = new ArrayList<>();

        // first guest is the announcer
        System.out.println("=================================================================================");
        System.out.println("\t\t\t\tBefore Game:\n");
        System.out.println("[Announcer]: \n\t\t\"I will keep track of how many cupcakes are handed out!\"");
        System.out.println("\t\t\"Only eat a cupcake if you have never had one.\"");
        System.out.println("=================================================================================");
        System.out.println("\t\t\tGame Start - Cupcake #1 is on the plate.");

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
            System.out.println("\n\t     \"I will eat this last cupcake.\"");
        System.out.println("=================================================================================");
        System.out.println("\t\t\t\tTo End Game:\n");
        System.out.println("[Announcer] to Minotaur: \n\t\t\"All guests have eaten and therefore visited!\"");
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