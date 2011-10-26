package shriekingMushroom.protocol.implementation.events;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.IProtocolConnection;
import shriekingMushroom.protocol.events.IProtoErrorEvent;

public class ProtocolErrorEvent<M extends IMessage> extends ProtocolEvent<M>
		implements IProtoErrorEvent<M> {

	public ProtocolErrorEvent(INetworkEvent e, IProtocolConnection<M> c) {
		super(e, c);
	}

}
