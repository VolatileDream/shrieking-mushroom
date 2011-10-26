package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.IProtocolConnection;
import protocol.events.IProtoErrorEvent;

public class ProtocolErrorEvent<M extends IMessage> extends ProtocolEvent<M>
		implements IProtoErrorEvent<M> {

	public ProtocolErrorEvent(INetworkEvent e, IProtocolConnection<M> c) {
		super(e, c);
	}

}
