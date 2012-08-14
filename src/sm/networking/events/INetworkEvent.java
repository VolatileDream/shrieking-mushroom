package sm.networking.events;

import shriekingmushroom.events.IEvent;
import sm.networking.IConnection;

public interface INetworkEvent extends IEvent {

	/**
	 * @return Returns the IConnection object that generated the event.
	 */
	public IConnection getConnection();

}
