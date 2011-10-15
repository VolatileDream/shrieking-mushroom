package networking.implementation.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import networking.IConnection;
import networking.TCPNetworkAccess;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetworkEvent;
import networking.implementation.ConnectionFactory;
import networking.implementation.NullConnection;
import networking.implementation.events.ConnectEvent;
import networking.implementation.events.ErrorEvent;
import networking.implementation.interfaces.InternalConnection;
import core.common.CommonAccessObject;
import core.events.IEventQueue;
import core.logging.ILogger.LogLevel;
import core.threading.IResetableStopper;
import core.threading.IRunner;
import core.threading.implementation.Stopper;

public class TCPConnectionPool implements TCPNetworkAccess {
	
	private final CommonAccessObject cao;
	private final ConnectionFactory fac;
	
	private final ArrayList<ConnectionThread> threads = new ArrayList<ConnectionThread>();
	
	private final IResetableStopper stop = new Stopper();
	
	public TCPConnectionPool( CommonAccessObject c ){
		cao = c;
		fac = new ConnectionFactory( cao );
	}
	
	void addConnection( final InternalConnection con, IEventQueue<INetworkEvent> queue ){
		
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
	public void connect( InetAddress net, int port, IEventQueue<INetworkEvent> queue ){
		Client c = new Client( fac, cao, net, port );
		
		try {
		
			InternalConnection con = c.Connect();
			this.addConnection( con, queue );

		} catch (IOException e) {
			cao.log.Log(e, LogLevel.Error);
			IConnection con = new NullConnection( net, port );
			INetErrorEvent event = new ErrorEvent( con );
			queue.offer( event );
		}
	}
	
	@Override
	public IRunner allowConnection( int port, IEventQueue<INetworkEvent> q ){
		return new Server( this, stop, cao, port, q );
	}
	
	@Override
	public ArrayList<IConnection> getConnections(){
		ArrayList<IConnection> connections = new ArrayList<IConnection>();
		for( ConnectionThread ct : threads ){
			connections.addAll( ct.getConnections() );
		}
		return connections;
	}
	
	@Override
	public void close(){
		stop.setStop();
	}
}
