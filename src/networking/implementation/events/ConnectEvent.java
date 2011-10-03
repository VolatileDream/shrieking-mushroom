package networking.implementation.events;

import networking.IConnection;
import networking.events.IConnectEvent;

public class ConnectEvent extends NetworkEvent implements IConnectEvent {

	public ConnectEvent(IConnection c) {
		super(c);
	}

}
