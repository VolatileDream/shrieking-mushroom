package core.events.implementation;

import core.events.IEvent;
import networking.interfaces.IConnection;

public abstract class Event implements IEvent {

	private final IConnection con;
	private final long time;
	
	public Event( IConnection c ){
		con = c;
		time = System.currentTimeMillis();
	}
	
	@Override
	public IConnection getConnection() {
		return con;
	}

	@Override
	public long getTimestamp() {
		return time;
	}

}
