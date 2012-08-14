package sm.protocol.implementation.events;

import sm.networking.events.INetworkEvent;
import sm.protocol.IMessage;
import sm.protocol.IProtocolConnection;
import sm.protocol.events.IProtoCloseEvent;

public class ProtocolCloseEvent<M extends IMessage> extends ProtocolEvent<M>
		implements IProtoCloseEvent<M> {

	public ProtocolCloseEvent(INetworkEvent e, IProtocolConnection<M> c) {
		super(e, c);
	}

}
