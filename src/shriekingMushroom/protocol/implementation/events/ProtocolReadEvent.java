package shriekingMushroom.protocol.implementation.events;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.IProtocolConnection;
import shriekingMushroom.protocol.events.IProtoReadEvent;

public class ProtocolReadEvent<M extends IMessage> extends ProtocolEvent<M>
		implements IProtoReadEvent<M> {

	private final M message;

	public ProtocolReadEvent(INetworkEvent e, IProtocolConnection<M> c, M m) {
		super(e, c);
		message = m;
	}

	@Override
	public M getMessage() {
		return message;
	}

}
