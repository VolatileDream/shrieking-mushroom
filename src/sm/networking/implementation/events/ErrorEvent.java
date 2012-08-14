package sm.networking.implementation.events;

import sm.networking.IConnection;
import sm.networking.events.INetErrorEvent;

public class ErrorEvent extends NetworkEvent implements INetErrorEvent {

	public ErrorEvent(IConnection c) {
		super(c);
	}

}
