package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IProtocolConnection;
import protocol.events.IProtocolEvent;
import protocol.implementation.interfaces.MyMessage;
import core.events.implementation.Event;

public abstract class ProtocolEvent extends Event implements IProtocolEvent<MyMessage> {

	private final IProtocolConnection<MyMessage> con;
	
	public ProtocolEvent( INetworkEvent e, IProtocolConnection<MyMessage> c ){
		super( e.getTimestamp() );
		con = c;
	}
	
	@Override
	public IProtocolConnection<MyMessage> getConnection() {
		return con;
	}

}
