package sm.protocol.implementation.events;

import sm.networking.events.INetworkEvent;
import sm.protocol.IMessage;
import sm.protocol.IProtocolConnection;
import sm.protocol.events.IProtoErrorEvent;

public class ProtocolErrorEvent<M extends IMessage> extends ProtocolEvent<M>
		implements IProtoErrorEvent<M> {

	public ProtocolErrorEvent(INetworkEvent e, IProtocolConnection<M> c) {
		super(e, c);
	}

}
