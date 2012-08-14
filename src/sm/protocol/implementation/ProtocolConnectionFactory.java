package sm.protocol.implementation;

import sm.CommonAccessObject;
import sm.networking.IConnection;
import sm.protocol.IMessage;
import sm.protocol.IMessageFactory;
import sm.protocol.IProtocolConnection;

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
