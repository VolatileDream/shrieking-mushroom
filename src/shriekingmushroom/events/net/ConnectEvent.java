package shriekingmushroom.events.net;

import shriekingmushroom.events.Event;
import shriekingmushroom.tcp.TcpConnection;

public class ConnectEvent extends Event {

	public ConnectEvent( TcpConnection conn ){
		super( conn );
	}
	
}
