package sm.protocol.implementation.events;

import sm.networking.events.INetworkEvent;
import sm.protocol.IMessage;
import sm.protocol.IProtocolConnection;
import sm.protocol.events.IProtoReadEvent;

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
