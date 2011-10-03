package networking.implementation.events;

import networking.IConnection;
import networking.events.INetworkEvent;
import core.events.implementation.Event;

public abstract class NetworkEvent extends Event implements INetworkEvent {

	private final IConnection con;
	
	public NetworkEvent( IConnection c ){
		con = c;
	}
	
	@Override
	public IConnection getConnection() {
		return con;
	}

}
