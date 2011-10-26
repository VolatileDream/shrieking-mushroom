package protocol.implementation;

import networking.IConnection;
import protocol.IMessage;
import protocol.IMessageFactory;
import protocol.IProtocolConnection;

public class ProtocolConnectionFactory<M extends IMessage> {

	private final IMessageFactory<M> factory;
	
	public ProtocolConnectionFactory( IMessageFactory<M> f ){
		factory=f;
	}
	
	public IProtocolConnection<M> transform( IConnection con ){
		return new ProtocolConnection<M>( con, factory );
	}
	
}
