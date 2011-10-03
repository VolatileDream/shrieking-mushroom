package core.events.implementation;

import core.events.IConnectEvent;
import networking.IConnection;

public class ConnectEvent extends Event implements IConnectEvent {

	public ConnectEvent(IConnection c) {
		super(c);
	}

}
