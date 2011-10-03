package networking;

import networking.implementation.unicast.TCPConnectionPool;
import core.CommonAccessObject;
import core.events.IEventQueue;

public class TCPNetworkFactory {
	
	private IEventQueue clientQ = null;
	private IEventQueue serverQ = null;
	private final CommonAccessObject cao;
	
	public TCPNetworkFactory( CommonAccessObject c ){
		cao = c;
	}
	
	public TCPNetworkFactory sameQueue( IEventQueue e ){
		clientQ = e;
		serverQ = e;
		return this;
	}
	
	public TCPNetworkFactory differentQueue( IEventQueue client, IEventQueue server ){
		clientQ = client;
		serverQ = server;
		return this;
	}
	
	public TCPNetworkAccess build() {
		if( clientQ == null || serverQ == null ){
			throw new RuntimeException("Requires both the client and server queues to be set");
		}
		return new TCPConnectionPool( cao, clientQ, serverQ );
	}
	
}
