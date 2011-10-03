package core.events.implementation;

import java.util.LinkedList;
import java.util.Queue;

import core.events.IEvent;
import core.events.IEventQueue;


public class EventQueue<M extends IEvent> implements IEventQueue<M> {

	private final Queue<M> queue = new LinkedList<M>();
	
	public synchronized boolean offer( M e ){
		boolean result = false;
		synchronized( queue ){
			result = queue.offer( e );
		}
		if( result ){
			notifyAll();
		}
		return result;
	}

	@Override
	public synchronized boolean poll() {
		synchronized( queue ){
			return !queue.isEmpty();
		}
	}

	@Override
	public synchronized M remove() {
		synchronized( queue ){
			return queue.poll();
		}
	}

	@Override
	public synchronized void waitFor(){
		while( ! poll() ){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
	}
	
}
