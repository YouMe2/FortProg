package set02;

import java.util.concurrent.Semaphore;

public class SyncPhilosopherS extends Thread {

	private int num;
	private Object left, right;

	private Semaphore ls, rs;

	public SyncPhilosopherS(int num, Semaphore left, Semaphore right) {
		this.num = num;
		this.ls = left;
		this.rs = right;
	}

	private void snooze() {
		 try {
		 Thread.sleep((long) (1000 * Math.random()));
		 } catch (InterruptedException e) {
		 }
	}

	public void run() {
		/*
		 * while (true) { System.out.println("Philosopher " + num + " is thinking");
		 * snooze(); synchronized (left) { synchronized (right) {
		 * System.out.println("Philosopher " + num + " is eating"); snooze(); } } }
		 */
		while (true) {
			// keine sticks
			System.out.println("Philosopher " + num + " is thinking");
			snooze();

			ls.acquireUninterruptibly();
			while (!rs.tryAcquire()) {
				ls.release();
//				snooze(); //?
				ls.acquireUninterruptibly();
			}
			// beide sticks
			System.out.println("Philosopher " + num + " is eating");
			snooze();
			rs.release();
			ls.release();
		}
	}

	public static void main(String[] args) {
		int count = 5;

		if (args.length > 0) {
			try {
				count = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
			}
		}

		Semaphore[] sticks = new Semaphore[count];
		SyncPhilosopherS[] phils = new SyncPhilosopherS[count];

		for (int i = 0; i < count; ++i) {
			sticks[i] = new Semaphore(1);
		}
		for (int i = 0; i < count; ++i) {
			phils[i] = new SyncPhilosopherS(i, sticks[i], sticks[(i + 1) % count]);
			phils[i].start();
		}
	}

}
