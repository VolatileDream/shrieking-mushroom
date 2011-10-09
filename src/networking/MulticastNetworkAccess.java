package networking;

import java.net.InetAddress;

import networking.events.INetworkEvent;
import core.events.IEventQueue;

public interface MulticastNetworkAccess {
	
	public MulticastSubscription subscribe( InetAddress net, int port, IEventQueue<INetworkEvent> e );
	
}
