package sm.networking;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

import shriekingmushroom.threading.IRunner;
import sm.networking.events.INetworkEvent;


public interface UDPNetworkAccess {

	public IRunner subscribe(InetAddress net, int port, BlockingQueue<INetworkEvent> e);

	/**
	 * Closes all UDP connections created with this network access
	 */
	public void close();

}
