package shriekingMushroom.protocol.implementation.events;

import shriekingMushroom.events.Event;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.IProtocolConnection;
import shriekingMushroom.protocol.events.IProtocolEvent;

public abstract class ProtocolEvent<M extends IMessage> extends Event implements
		IProtocolEvent<M> {

	private final IProtocolConnection<M> con;

	public ProtocolEvent(INetworkEvent e, IProtocolConnection<M> c) {
		super(e.getTimestamp());
		con = c;
	}

	@Override
	public IProtocolConnection<M> getConnection() {
		return con;
	}

}
