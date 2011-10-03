package core.events.implementation;

import core.events.IErrorEvent;
import networking.IConnection;

public class ErrorEvent extends Event implements IErrorEvent {

	public ErrorEvent(IConnection c) {
		super(c);
	}

}
