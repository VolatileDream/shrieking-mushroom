package networking;

import java.net.InetAddress;

import networking.events.INetworkEvent;
import core.events.IEventQueue;
import core.threading.IRunner;

public interface MulticastNetworkAccess {

	public IRunner subscribe(InetAddress net, int port,
			IEventQueue<INetworkEvent> e);

	/**
	 * Closes all connections created with this network access
	 */
	public void close();

}
