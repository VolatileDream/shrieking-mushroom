package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.IProtocolConnection;
import protocol.events.IProtoCloseEvent;

public class ProtocolCloseEvent<M extends IMessage> extends ProtocolEvent<M>
		implements IProtoCloseEvent<M> {

	public ProtocolCloseEvent(INetworkEvent e, IProtocolConnection<M> c) {
		super(e, c);
	}

}
