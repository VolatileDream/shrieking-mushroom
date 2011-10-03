package networking.events;

import core.events.IEvent;
import networking.IConnection;

public interface INetworkEvent extends IEvent {

	/**
	 * @return Returns the IConnection object that generated the event.
	 */
	public IConnection getConnection();
	
}
