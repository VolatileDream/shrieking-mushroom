package shriekingMushroom.networking;

import java.net.InetAddress;

import shriekingMushroom.events.IEventQueue;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.threading.IRunner;


public interface MulticastNetworkAccess {

	public IRunner subscribe(InetAddress net, int port,
			IEventQueue<INetworkEvent> e);

	/**
	 * Closes all connections created with this network access
	 */
	public void close();

}
