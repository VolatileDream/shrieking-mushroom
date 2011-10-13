package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.IProtocolConnection;
import protocol.events.IProtoConnectEvent;

public class ProtocolConnectEvent<M extends IMessage> extends ProtocolEvent<M> implements IProtoConnectEvent<M> {

	public ProtocolConnectEvent(INetworkEvent e, IProtocolConnection<M> c ) {
		super(e, c);
	}

}
