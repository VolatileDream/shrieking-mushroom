package networking.events;

import networking.IConnection;
import core.events.IEvent;

public interface INetworkEvent extends IEvent {

	/**
	 * @return Returns the IConnection object that generated the event.
	 */
	public IConnection getConnection();

}
