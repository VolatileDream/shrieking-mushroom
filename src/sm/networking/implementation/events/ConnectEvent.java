package sm.networking.implementation.events;

import sm.networking.IConnection;
import sm.networking.events.INetConnectEvent;

public class ConnectEvent extends NetworkEvent implements INetConnectEvent {

	public ConnectEvent(IConnection c) {
		super(c);
	}

}
