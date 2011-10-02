package events.implementation;

import java.util.LinkedList;
import java.util.Queue;

import events.IEvent;
import events.IEventQueue;

public class EventQueue implements IEventQueue {

	private final Queue<IEvent> queue = new LinkedList<IEvent>();
	
	public synchronized boolean offer( IEvent e ){
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
	public synchronized IEvent remove() {
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
