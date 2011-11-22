package shriekingMushroom.networking;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.BlockingQueue;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.threading.IRunner;


public interface MulticastNetworkAccess {

	public IRunner subscribe(NetworkInterface nif, InetAddress net, int port, BlockingQueue<INetworkEvent> e);

	/**
	 * Closes all connections created with this network access
	 */
	public void close();

}
