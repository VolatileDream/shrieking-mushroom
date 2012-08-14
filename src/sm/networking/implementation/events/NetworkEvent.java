package sm.networking.implementation.events;

import shriekingmushroom.events.Event;
import sm.networking.IConnection;
import sm.networking.events.INetworkEvent;

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
