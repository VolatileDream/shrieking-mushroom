package shriekingMushroom.events.implementation;

import java.util.LinkedList;
import java.util.Queue;

import shriekingMushroom.events.IEvent;
import shriekingMushroom.events.IEventQueue;
import shriekingMushroom.threading.IStopper;


public class EventQueue<M extends IEvent> implements IEventQueue<M> {

	private final Queue<M> queue = new LinkedList<M>();

	public synchronized boolean offer(M e) {
		boolean result = false;
		synchronized (queue) {
			result = queue.offer(e);
		}
		if (result) {
			notifyAll();
		}
		return result;
	}

	@Override
	public synchronized boolean poll() {
		synchronized (queue) {
			return !queue.isEmpty();
		}
	}

	@Override
	public synchronized M remove() {
		synchronized (queue) {
			return queue.poll();
		}
	}

	@Override
	public synchronized void waitFor(IStopper stop) {
		while (!poll() && (stop == null || !stop.hasStopped())) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

}
