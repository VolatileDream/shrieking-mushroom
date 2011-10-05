package networking.implementation.events;

import networking.IConnection;
import networking.events.INetErrorEvent;

public class ErrorEvent extends NetworkEvent implements INetErrorEvent {

	public ErrorEvent(IConnection c) {
		super(c);
	}

}
