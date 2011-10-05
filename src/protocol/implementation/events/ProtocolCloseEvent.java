package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IProtocolConnection;
import protocol.events.IProtoCloseEvent;
import protocol.implementation.interfaces.MyMessage;

public class ProtocolCloseEvent extends ProtocolEvent implements IProtoCloseEvent<MyMessage> {

	public ProtocolCloseEvent(INetworkEvent e, IProtocolConnection<MyMessage> c ) {
		super(e, c);
	}

}
