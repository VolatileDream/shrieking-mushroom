package events.implementation;

import events.IErrorEvent;
import networking.interfaces.IConnection;

public class ErrorEvent extends Event implements IErrorEvent {

	public ErrorEvent(IConnection c) {
		super(c);
	}

}
