package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import networking.interfaces.IConnection;
import networking.interfaces.TCPNetworkAccess;
import networking.interfaces.TCPServer;
import networking.io.ConnectionFactory;
import networking.io.InternalConnection;
import networking.io.implementation.NullConnection;
import core.CommonAccessObject;
import core.logging.ILogger.LogLevel;
import events.IConnectEvent;
import events.IErrorEvent;
import events.IEventQueue;
import events.implementation.ConnectEvent;
import events.implementation.ErrorEvent;

public class TCPConnectionPool implements TCPNetworkAccess {
	
	private final IEventQueue clientQueue;
	private final IEventQueue serverQueue;
	private final CommonAccessObject cao;
	private final ConnectionFactory fac;
	
	private final ArrayList<ConnectionThread> serverThreads = new ArrayList<ConnectionThread>();
	private final ArrayList<ConnectionThread> clientThreads = new ArrayList<ConnectionThread>();
	
	private Boolean keepRunning = true;
	
	public TCPConnectionPool( CommonAccessObject c, IEventQueue clientQ, IEventQueue serverQ ){
		clientQueue = clientQ;
		serverQueue = serverQ;
		cao = c;
		fac = new ConnectionFactory( cao );
	}
	
	public void addClientConnection( InternalConnection c ){
		addConnection( c, clientThreads, clientQueue );
	}
	
	public void addServerConnection( InternalConnection c ){
		addConnection( c, serverThreads, serverQueue );
	}
	
	private void addConnection( final InternalConnection con, final ArrayList<ConnectionThread> threads, final IEventQueue queue ){
		
		boolean added = false;
		
		for( ConnectionThread ct : threads ){
			
			added = ct.addConnection( con );
			if( added ){
				break;
			}
		}
		
		if( !added && keepRunning ){
			
			ConnectionThread ct = newThread( threads, queue );
			
			added = ct.addConnection( con );
			
			if( ! added ){
				cao.log.Log( "Unable to add the connection to a new thread", LogLevel.Fatal );
			}
		}
		if( added ){
			IConnectEvent event = new ConnectEvent( con );
			queue.offer( event );
		}else{
			try {
				cao.log.Log("Forced to close a connection", LogLevel.Error );
				con.close();
			} catch (IOException e) {}
		}
	}

	private ConnectionThread newThread( ArrayList<ConnectionThread> threads, IEventQueue e ){
		ConnectionThread ct = new ConnectionThread( cao, e );
		
		Thread t = new Thread( ct );
		t.start();
		
		threads.add( ct );
		
		return ct;
	}
	
	// interfaces
	
	@Override
	public IEventQueue getClientQueue(){
		return clientQueue;
	}
	
	@Override
	public IEventQueue getServerQueue(){
		return serverQueue;
	}
	
	@Override
	public void connect( InetAddress net, int port ){
		Client c = new Client( fac, cao, net, port );
		
		try {
		
			InternalConnection con = c.Connect();
			this.addClientConnection( con );

		} catch (IOException e) {
			cao.log.Log(e, LogLevel.Error);
			IConnection con = new NullConnection( net, port );
			IErrorEvent event = new ErrorEvent( con );
			clientQueue.offer( event );
		}
	}
	
	@Override
	public TCPServer allowConnection( int port ){
		return new Server( this, cao, port );
	}
	
	@Override
	public ArrayList<IConnection> getConnections(){
		ArrayList<IConnection> connections = new ArrayList<IConnection>();
		for( ConnectionThread ct : serverThreads ){
			connections.addAll( ct.getConnections() );
		}
		for( ConnectionThread ct : clientThreads ){
			connections.addAll( ct.getConnections() );
		}
		return connections;
	}
	
	@Override
	public void close(){
		
		synchronized( keepRunning ){
			keepRunning = false;
		}
		
		for( ConnectionThread ct : serverThreads ){
			ct.close();
		}
		for( ConnectionThread ct : clientThreads ){
			ct.close();
		}
		
	}
}
