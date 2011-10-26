package shriekingMushroom.core.events.implementation;

import shriekingMushroom.core.events.IEvent;

public abstract class Event implements IEvent {

	private final long time;

	public Event() {
		time = System.currentTimeMillis();
	}

	protected Event(long l) {
		time = l;
	}

	@Override
	public long getTimestamp() {
		return time;
	}

}