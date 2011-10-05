package networking.implementation.unicast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import networking.IConnection;
import networking.TCPNetworkAccess;
import networking.TCPServer;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetworkEvent;
import networking.implementation.ConnectionFactory;
import networking.implementation.NullConnection;
import networking.implementation.events.ConnectEvent;
import networking.implementation.events.ErrorEvent;
import networking.implementation.interfaces.InternalConnection;
import core.CommonAccessObject;
import core.events.IEventQueue;
import core.logging.ILogger.LogLevel;
import core.threading.IStopper;
import core.threading.implementation.Stopper;

public class TCPConnectionPool implements TCPNetworkAccess {
	
	private final IEventQueue<INetworkEvent> clientQueue;
	private final IEventQueue<INetworkEvent> serverQueue;
	private final CommonAccessObject cao;
	private final ConnectionFactory fac;
	
	private final ArrayList<ConnectionThread> serverThreads = new ArrayList<ConnectionThread>();
	private final ArrayList<ConnectionThread> clientThreads = new ArrayList<ConnectionThread>();
	
	private final IStopper stop = new Stopper();
	
	public TCPConnectionPool( CommonAccessObject c, IEventQueue<INetworkEvent> clientQ, IEventQueue<INetworkEvent> serverQ ){
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
	
	private void addConnection( final InternalConnection con, final ArrayList<ConnectionThread> threads, final IEventQueue<INetworkEvent> queue ){
		
		boolean added = false;
		
		for( ConnectionThread ct : threads ){
			
			added = ct.addConnection( con );
			if( added ){
				break;
			}
		}
		
		if( !added && ! stop.hasStopped() ){
			
			ConnectionThread ct = newThread( threads, queue );
			
			added = ct.addConnection( con );
			
			if( ! added ){
				cao.log.Log( "Unable to add the connection to a new thread", LogLevel.Fatal );
			}
		}
		if( added ){
			INetConnectEvent event = new ConnectEvent( con );
			queue.offer( event );
		}else{
			try {
				cao.log.Log("Forced to close a connection", LogLevel.Error );
				con.close();
			} catch (IOException e) {}
		}
	}

	private ConnectionThread newThread( ArrayList<ConnectionThread> threads, IEventQueue<INetworkEvent> e ){
		ConnectionThread ct = new ConnectionThread( cao, e, stop );
		
		Thread t = new Thread( ct );
		t.start();
		
		threads.add( ct );
		
		return ct;
	}
	
	// interfaces
	
	@Override
	public IEventQueue<INetworkEvent> getClientQueue(){
		return clientQueue;
	}
	
	@Override
	public IEventQueue<INetworkEvent> getServerQueue(){
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
			INetErrorEvent event = new ErrorEvent( con );
			clientQueue.offer( event );
		}
	}
	
	@Override
	public TCPServer allowConnection( int port ){
		return new Server( this, stop, cao, port );
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
		
		stop.setStop();
		/*
		//Don't need this because IStopper.setStop() will stop all of it's copies as well
		for( ConnectionThread ct : serverThreads ){
			ct.close();
		}
		for( ConnectionThread ct : clientThreads ){
			ct.close();
		}
		*/
	}
}
