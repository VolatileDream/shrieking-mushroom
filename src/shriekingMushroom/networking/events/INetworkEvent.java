package shriekingMushroom.networking.events;

import shriekingMushroom.events.IEvent;
import shriekingMushroom.networking.IConnection;

public interface INetworkEvent extends IEvent {

	/**
	 * @return Returns the IConnection object that generated the event.
	 */
	public IConnection getConnection();

}
