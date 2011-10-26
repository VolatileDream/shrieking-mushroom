package shriekingMushroom.protocol.implementation.events;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.IProtocolConnection;
import shriekingMushroom.protocol.events.IProtoCloseEvent;

public class ProtocolCloseEvent<M extends IMessage> extends ProtocolEvent<M>
		implements IProtoCloseEvent<M> {

	public ProtocolCloseEvent(INetworkEvent e, IProtocolConnection<M> c) {
		super(e, c);
	}

}
