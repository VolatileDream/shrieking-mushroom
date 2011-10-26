package shriekingMushroom.core.events;

import shriekingMushroom.core.threading.IStopper;

public interface IEventQueue<M extends IEvent> {

	/**
	 * Attempts to add an event to the queue
	 * 
	 * @param e
	 *            The event to add to the queue
	 * @return Returns true if the event was successfully added
	 */
	public boolean offer(M e);

	/**
	 * Gets and removes the top element from the queue
	 * 
	 * @return Returns the top event from the queue, returns null if there are
	 *         no events in the queue.
	 */
	public M remove();

	/**
	 * Checks if there are events in the queue
	 * 
	 * @return Returns true if there are events in the queue
	 */
	public boolean poll();

	/**
	 * Waits until there is an event in the queue, or until the stopper's
	 * hasStopped returns true.
	 */
	public void waitFor(IStopper stop);
}
