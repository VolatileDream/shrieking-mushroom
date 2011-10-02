package events.implementation;

import networking.interfaces.IConnection;
import events.IConnectEvent;

public class ConnectEvent extends Event implements IConnectEvent {

	public ConnectEvent(IConnection c) {
		super(c);
	}

}
