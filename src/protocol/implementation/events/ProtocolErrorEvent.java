package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IProtocolConnection;
import protocol.events.IProtoErrorEvent;
import protocol.implementation.interfaces.MyMessage;

public class ProtocolErrorEvent extends ProtocolEvent implements IProtoErrorEvent<MyMessage> {

	public ProtocolErrorEvent(INetworkEvent e, IProtocolConnection<MyMessage> c ) {
		super(e, c);
	}

}
