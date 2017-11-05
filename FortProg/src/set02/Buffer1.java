package set02;

import java.util.concurrent.TimeoutException;

/**
 * Single element buffer with synchronization.
 *
 * @author FortProg team
 * @param <E>
 *            Type of the element
 */
public class Buffer1<E> {

	// element + empty flag
	private E content;
	private boolean empty;

	// synchronization objects
	private Object r = new Object();
	private Object w = new Object();

	public Buffer1() {
		empty = true;
	}

	public Buffer1(E content) {
		this.content = content;
		empty = false;
	}

	/**
	 * take the element from the buffer; suspends on an empty buffer.
	 *
	 * @return element of the buffer
	 * @throws InterruptedException
	 */
	public E take() throws InterruptedException {
		synchronized (r) {
			while (empty)
				r.wait();
			synchronized (w) {
				empty = true;
				w.notify();
				return content;
			}
		}
	}

	/**
	 * put an element into the buffer; suspends on a full buffer
	 *
	 * @param o
	 *            Object to put into
	 * @throws InterruptedException
	 */
	public void put(E o) throws InterruptedException {
		synchronized (w) {
			while (!empty)
				w.wait();
			synchronized (r) {
				content = o;
				empty = false;
				r.notify();
			}
		}
	}

	/**
	 * Return whether the buffer is empty
	 *
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * Read the element from the buffer without emptying it; suspends on an empty
	 * buffer.
	 *
	 * @return element of the buffer
	 * @throws InterruptedException
	 */
	public E read() throws InterruptedException {
		synchronized (r) {
			while (empty)
				r.wait();
			synchronized (w) {
				return content;
			}
		}
	}

	/**
	 * Try to put an element into the buffer; succeeds only for an empty buffer
	 *
	 * @param elem
	 *            Element to put into
	 * @return true if successful
	 */
	public boolean tryPut(E elem) {
		synchronized (w) {
			boolean canPut = empty;
			if (canPut)
				synchronized (r) {
					content = elem;
					empty = false;
					r.notify();
				}
			return canPut;
		}
	}

	/**
	 * Overwrite the element in the buffer, even if if the buffer is empty
	 *
	 * @param elem
	 *            Element to overwrite with
	 */
	public void overwrite(E elem) {
		synchronized (w) {
			synchronized (r) {
				content = elem;
				empty = false;
				r.notify();
			}
		}
	}

	/**
	 * take with timeout. The timeout mechanism has to be handcrafted as there is
	 * no way to detect whether a wait() was left because of a timeout or a
	 * notify().
	 *
	 * @param timeout
	 *            Maximum time to wait in milliseconds
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 *             if a timeout occurred
	 */
	public E take(long timeout) throws InterruptedException, TimeoutException {
		Object lock = new Object();
		Thread current = Thread.currentThread();
		// QND to create mutable long since it needs to be set from inner class
		long[] interruptThreadId = { current.getId() };
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					/*
					 * Make sure that "current" will not be interrupted after
					 * returning from take (and possibly entering another wait or
					 * sleep somewhere which would be interrupted then)
					 */
					synchronized (lock) {
						if (!isInterrupted()) {
							interruptThreadId[0] = getId();
							current.interrupt();
						}
					}
				} catch (InterruptedException e) {
					/*
					 * Oops, we're late! take seems to have already returned,
					 * possibly by being interrupted from the outside world. In
					 * that case, we're done here.
					 */
				}
			}
		};
		t.start();
		try {
			/*
			 * Actually perform the take operation
			 */
			return take();
		} catch (InterruptedException e) {
			if (interruptThreadId[0] == t.getId())
				/*
				 * It was our timeout thread that interrupted the take operation,
				 * so we should throw a different exception instead:
				 */
				throw new TimeoutException("timed out");
			else // interruptThreadId[0] == current.getId()
				/*
				 * During our take operation the thread "current" could be
				 * interrupted from the outside world. In that case we have to
				 * throw the original interrupted exception. Otherwise,
				 * interrupting this version of take would always lead to a
				 * timeout exception.
				 */
				throw e;
		} finally {
			synchronized (lock) {
				/*
				 * Make sure our timeout thread won't interrupt us after we
				 * returned. To prevent this, we have to interrupt it first!
				 */
				t.interrupt();
			}
		}
	}
}
