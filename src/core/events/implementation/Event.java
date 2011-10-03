package core.events.implementation;

import core.events.IEvent;

public abstract class Event implements IEvent {

	private final long time;
	
	public Event(){
		time = System.currentTimeMillis();
	}

	@Override
	public long getTimestamp() {
		return time;
	}

}
