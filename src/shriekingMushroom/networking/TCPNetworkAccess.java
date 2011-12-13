package shriekingMushroom.networking;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.threading.IRestartable;


public interface TCPNetworkAccess {

	public ArrayList<IConnection> getConnections();

	public void connect(InetAddress net, int port, BlockingQueue<INetworkEvent> e);

	/**
	 * Open a port on the machine to accept connections, and puts events into
	 * the queue passed in.
	 * 
	 * @param port
	 *            The port to open the connections on
	 * @param e
	 *            Event Queue to use
	 * @return Returns an IRunner that controls the server
	 */
	public IRestartable allowConnection(int port, BlockingQueue<INetworkEvent> e);

	/**
	 * Closes all connections that this TCP Network Access has created using
	 * either connect(InetAddress,int) or allowConnection(int)
	 */
	public void close();
}
