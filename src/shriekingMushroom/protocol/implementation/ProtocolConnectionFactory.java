package shriekingMushroom.protocol.implementation;

import shriekingMushroom.CommonAccessObject;
import shriekingMushroom.networking.IConnection;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.IMessageFactory;
import shriekingMushroom.protocol.IProtocolConnection;

public class ProtocolConnectionFactory<M extends IMessage> {

	private final CommonAccessObject cao;
	private final IMessageFactory<M> factory;

	public ProtocolConnectionFactory( CommonAccessObject cao, IMessageFactory<M> f) {
		factory = f;
		this.cao = cao;
	}

	public IProtocolConnection<M> transform(IConnection con) {
		return new ProtocolConnection<M>(cao, con, factory);
	}

}
