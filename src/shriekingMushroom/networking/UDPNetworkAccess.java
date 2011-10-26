package shriekingMushroom.networking;

import java.net.InetAddress;

import shriekingMushroom.events.IEventQueue;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.threading.IRunner;


public interface UDPNetworkAccess {

	public IRunner subscribe(InetAddress net, int port,
			IEventQueue<INetworkEvent> e);

	/**
	 * Closes all UDP connections created with this network access
	 */
	public void close();

}
