package set02;

public class SyncPhilosopher extends Thread {

    private int num;
    private Object left, right;

    public SyncPhilosopher(int num, Object left, Object right) {
        this.num = num;
        this.left = left;
        this.right = right;
    }

    private void snooze() {
        try {
            Thread.sleep((long) (1000 * Math.random()));
        } catch (InterruptedException _) {
        }
    }

    public void run() {
        while (true) {
            System.out.println("Philosopher " + num + " is thinking");
            snooze();
            synchronized (left) {
                synchronized (right) {
                    System.out.println("Philosopher " + num + " is eating");
                    snooze();
                }
            }
        }
    }

    public static void main(String[] args) {
        int count = 5;

        if (args.length > 0) {
            try {
                count = Integer.parseInt(args[0]);
            } catch (NumberFormatException _) {
            }
        }

        Object[] sticks = new Object[count];
        SyncPhilosopher[] phils = new SyncPhilosopher[count];

        for (int i = 0; i < count; ++i) {
            sticks[i] = new Object();
        }

        for (int i = 0; i < count; ++i) {
            phils[i] = new SyncPhilosopher(i, sticks[i],
                    sticks[(i + 1) % count]);
            phils[i].start();
        }
    }

}
