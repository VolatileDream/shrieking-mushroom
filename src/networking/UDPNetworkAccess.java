package networking;

import java.net.InetAddress;

import networking.events.INetworkEvent;
import core.events.IEventQueue;
import core.threading.IRunner;

public interface UDPNetworkAccess {
	
	public IRunner subscribeToMulticast( InetAddress net, int port, IEventQueue<INetworkEvent> e );
	
	public IRunner subscribeToUDP( InetAddress net, int port, IEventQueue<INetworkEvent> e );
}
