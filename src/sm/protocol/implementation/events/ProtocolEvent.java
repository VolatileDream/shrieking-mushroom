package sm.protocol.implementation.events;

import shriekingmushroom.events.Event;
import sm.networking.events.INetworkEvent;
import sm.protocol.IMessage;
import sm.protocol.IProtocolConnection;
import sm.protocol.events.IProtocolEvent;

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
