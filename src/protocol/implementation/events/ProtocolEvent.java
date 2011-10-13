package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.IProtocolConnection;
import protocol.events.IProtocolEvent;
import core.events.implementation.Event;

public abstract class ProtocolEvent<M extends IMessage> extends Event implements IProtocolEvent<M> {

	private final IProtocolConnection<M> con;
	
	public ProtocolEvent( INetworkEvent e, IProtocolConnection<M> c ){
		super( e.getTimestamp() );
		con = c;
	}
	
	@Override
	public IProtocolConnection<M> getConnection() {
		return con;
	}

}
