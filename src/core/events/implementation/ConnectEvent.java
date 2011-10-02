package core.events.implementation;

import core.events.IConnectEvent;
import networking.interfaces.IConnection;

public class ConnectEvent extends Event implements IConnectEvent {

	public ConnectEvent(IConnection c) {
		super(c);
	}

}
