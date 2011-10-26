package shriekingMushroom.networking;

import java.net.InetAddress;

import shriekingMushroom.core.events.IEventQueue;
import shriekingMushroom.core.threading.IRunner;
import shriekingMushroom.networking.events.INetworkEvent;


public interface UDPNetworkAccess {

	public IRunner subscribe(InetAddress net, int port,
			IEventQueue<INetworkEvent> e);

	/**
	 * Closes all UDP connections created with this network access
	 */
	public void close();

}
