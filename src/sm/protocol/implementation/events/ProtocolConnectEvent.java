package sm.protocol.implementation.events;

import sm.networking.events.INetworkEvent;
import sm.protocol.IMessage;
import sm.protocol.IProtocolConnection;
import sm.protocol.events.IProtoConnectEvent;

public class ProtocolConnectEvent<M extends IMessage> extends ProtocolEvent<M>
		implements IProtoConnectEvent<M> {

	public ProtocolConnectEvent(INetworkEvent e, IProtocolConnection<M> c) {
		super(e, c);
		
	}

}
