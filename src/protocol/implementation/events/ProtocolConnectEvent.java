package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IProtocolConnection;
import protocol.events.IProtoConnectEvent;
import protocol.implementation.interfaces.MyMessage;

public class ProtocolConnectEvent extends ProtocolEvent implements IProtoConnectEvent<MyMessage> {

	public ProtocolConnectEvent(INetworkEvent e, IProtocolConnection<MyMessage> c ) {
		super(e, c);
	}

}
