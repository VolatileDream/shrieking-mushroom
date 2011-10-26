package shriekingMushroom.networking.implementation.events;

import shriekingMushroom.networking.IConnection;
import shriekingMushroom.networking.events.INetConnectEvent;

public class ConnectEvent extends NetworkEvent implements INetConnectEvent {

	public ConnectEvent(IConnection c) {
		super(c);
	}

}
