package networking.implementation.events;

import networking.IConnection;
import networking.events.IErrorEvent;

public class ErrorEvent extends NetworkEvent implements IErrorEvent {

	public ErrorEvent(IConnection c) {
		super(c);
	}

}
