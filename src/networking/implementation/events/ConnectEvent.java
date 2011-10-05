package networking.implementation.events;

import networking.IConnection;
import networking.events.INetConnectEvent;

public class ConnectEvent extends NetworkEvent implements INetConnectEvent {

	public ConnectEvent(IConnection c) {
		super(c);
	}

}
