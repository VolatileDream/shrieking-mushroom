package shriekingMushroom.networking.implementation.events;

import shriekingMushroom.core.events.implementation.Event;
import shriekingMushroom.networking.IConnection;
import shriekingMushroom.networking.events.INetworkEvent;

public abstract class NetworkEvent extends Event implements INetworkEvent {

	private final IConnection con;

	public NetworkEvent(IConnection c) {
		con = c;
	}

	@Override
	public IConnection getConnection() {
		return con;
	}

}
