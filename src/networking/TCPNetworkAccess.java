package networking;

import java.net.InetAddress;
import java.util.ArrayList;

import networking.events.INetworkEvent;

import core.events.*;
import core.threading.IRunner;



public interface TCPNetworkAccess {
	
	public ArrayList<IConnection> getConnections();
	
	public void connect( InetAddress net, int port, IEventQueue<INetworkEvent> e );
	
	/**
	 * Open a port on the machine to accept connections, and puts events into the queue passed in.
	 * @param port The port to open the connections on
	 * @param e Event Queue to use
	 * @return Returns an IRunner that controls the server
	 */
	public IRunner allowConnection( int port, IEventQueue<INetworkEvent> e );
	
	/**
	 * Closes all connections that this TCP Network
	 * Access has created using either connect(InetAddress,int) or allowConnection(int)
	 */
	public void close();
}
