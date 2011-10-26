package shriekingMushroom.networking.implementation.events;

import shriekingMushroom.networking.IConnection;
import shriekingMushroom.networking.events.INetErrorEvent;

public class ErrorEvent extends NetworkEvent implements INetErrorEvent {

	public ErrorEvent(IConnection c) {
		super(c);
	}

}
