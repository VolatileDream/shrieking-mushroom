package networking;

import java.net.InetAddress;
import java.util.ArrayList;

import networking.events.INetworkEvent;

import core.events.*;



public interface TCPNetworkAccess {
	
	/**
	 * Gets the event queue for all the connections started with 'connect(InetAddress,int)'
	 */
	public IEventQueue<INetworkEvent> getClientQueue();
	/**
	 * Gets the event queue for all the connections started with 'allowConnection(int)'
	 */
	public IEventQueue<INetworkEvent> getServerQueue();
	
	public ArrayList<IConnection> getConnections();
	
	public void connect( InetAddress net, int port );
	
	public TCPServer allowConnection( int port );
	
	/**
	 * Closes all connections that this TCP Network
	 * Access has created using either connect(InetAddress,int) or allowConnection(int)
	 */
	public void close();
}
