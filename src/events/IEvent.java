package events;

import networking.interfaces.IConnection;

public interface IEvent {
	
	public long getTimestamp();
	
	/**
	 * @return Returns the IConnection object that generated the event.
	 */
	public IConnection getConnection();
	
}
