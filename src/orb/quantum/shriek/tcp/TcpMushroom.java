package orb.quantum.shriek.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import orb.quantum.shriek.common.RemovableKey;
import orb.quantum.shriek.events.EventBuilder;
import orb.quantum.shriek.threading.Stopable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class TcpMushroom implements Stopable {
	
	private static final Logger logger = LogManager.getLogger( TcpMushroom.class );
	
	private EventBuilder builder;
	
	private TcpThread server;
	private TcpThread client;
	
	public TcpMushroom( EventBuilder builder ) throws IOException {
		this.builder = builder;
	}
	
	private TcpThread startThread( Selector select ){
		
		logger.debug("Creating TcpThread");
		
		TcpThread tcp = new TcpThread(this, select);
		
		Thread th = new Thread( tcp );
		th.start();
		
		return tcp;
	}
	
	public void connect( InetAddress addr, int port ) throws IOException {
		
		logger.debug("Connecting to {}:{}", addr, port );
		
		SocketChannel chan = SocketChannel.open();
		
		chan.connect( new InetSocketAddress( addr, port ) );
		
		createTcpConnection(chan);
	}
	
	TcpConnection createTcpConnection( SocketChannel chan ) throws IOException {
		
		synchronized (this) {
			// create the client on demand
			if (client == null) {
				client = startThread(Selector.open());
			}
		}
		chan.configureBlocking(false);
		
		TcpConnection con = new TcpConnection( this.eventBuilder() );
		
		SelectionKey key = client.register(
				chan
				,SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE
				, con
			);
		
		con.attach( key );
		key.attach( con );
		
		return con;
	}
	
	public AutoCloseable listen( int port ) throws IOException {
		
		logger.debug("Starting to listen on port {}", port );
		
		synchronized (this) {
			// create server on demand
			if (server == null) {
				server = startThread(Selector.open());
			}
		}
		ServerSocketChannel chan = ServerSocketChannel.open();
		
		//bind to port
		chan.bind( new InetSocketAddress(port) );
		
		// non-blocking
		chan.configureBlocking(false);

		SelectionKey key = server.register( chan, SelectionKey.OP_ACCEPT );
		
		return new RemovableKey( key );
	}
	
	public EventBuilder eventBuilder(){
		return builder;
	}

	@Override
	public void stop() {
		synchronized (this) {
			if (client != null) {
				client.stop();
				client = null;
			}
			if (server != null) {
				server.stop();
				server = null;
			}
		}
		
	}
	
}
