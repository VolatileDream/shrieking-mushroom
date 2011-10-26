package shriekingMushroom.protocol.implementation.events;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.IProtocolConnection;
import shriekingMushroom.protocol.events.IProtoConnectEvent;

public class ProtocolConnectEvent<M extends IMessage> extends ProtocolEvent<M>
		implements IProtoConnectEvent<M> {

	public ProtocolConnectEvent(INetworkEvent e, IProtocolConnection<M> c) {
		super(e, c);
		
	}

}
