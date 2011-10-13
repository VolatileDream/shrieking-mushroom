package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.IProtocolConnection;
import protocol.events.IProtoReadEvent;

public class ProtocolReadEvent<M extends IMessage> extends ProtocolEvent<M> implements IProtoReadEvent<M> {

	private final M message;
	
	public ProtocolReadEvent(INetworkEvent e, IProtocolConnection<M> c, M m ) {
		super(e, c);
		message = m;
	}

	@Override
	public M getMessage() {
		return message;
	}

}
