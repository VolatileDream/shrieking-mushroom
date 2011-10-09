package networking;

import java.net.InetAddress;
import java.util.ArrayList;

import networking.events.INetworkEvent;

import core.events.*;



public interface TCPNetworkAccess {
	
	public ArrayList<IConnection> getConnections();
	
	public void connect( InetAddress net, int port, IEventQueue<INetworkEvent> e );
	
	public TCPServer allowConnection( int port, IEventQueue<INetworkEvent> e );
	
	/**
	 * Closes all connections that this TCP Network
	 * Access has created using either connect(InetAddress,int) or allowConnection(int)
	 */
	public void close();
}
