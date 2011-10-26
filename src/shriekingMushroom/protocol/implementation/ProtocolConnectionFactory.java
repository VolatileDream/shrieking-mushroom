package shriekingMushroom.protocol.implementation;

import shriekingMushroom.networking.IConnection;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.IMessageFactory;
import shriekingMushroom.protocol.IProtocolConnection;

public class ProtocolConnectionFactory<M extends IMessage> {

	private final IMessageFactory<M> factory;

	public ProtocolConnectionFactory(IMessageFactory<M> f) {
		factory = f;
	}

	public IProtocolConnection<M> transform(IConnection con) {
		return new ProtocolConnection<M>(con, factory);
	}

}
