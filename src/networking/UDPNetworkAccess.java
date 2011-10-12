package networking;

import java.net.InetAddress;

import networking.events.INetworkEvent;
import core.events.IEventQueue;
import core.threading.IRunner;

public interface UDPNetworkAccess {
	
	public IRunner subscribe( InetAddress net, int port, IEventQueue<INetworkEvent> e );
}
