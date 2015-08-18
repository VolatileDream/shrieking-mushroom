package orb.quantum.shriek.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import orb.quantum.shriek.common.RemovableKey;
import orb.quantum.shriek.events.EventBuilder;
import orb.quantum.shriek.threading.ChannelThread;
import orb.quantum.shriek.threading.IoHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpMushroom {
	
	private static final Logger logger = LogManager.getLogger( TcpMushroom.class );
	
	final EventBuilder builder;
	final ChannelThread thread;
	final IoHandler handler;	
	
	
	public TcpMushroom( EventBuilder builder, ChannelThread thread ) throws IOException {
		this.builder = builder;
		this.thread = thread;
		this.handler = new TcpHandler(this);
	}
		
	public void connect( InetAddress addr, int port ) throws IOException {
		
		logger.debug("Connecting to {}:{}", addr, port );
		
		SocketChannel chan = SocketChannel.open();
		
		chan.connect( new InetSocketAddress( addr, port ) );
		
		createTcpConnection(chan);
	}
	
	TcpConnection createTcpConnection( SocketChannel chan ) throws IOException {
		
		chan.configureBlocking(false);
		
		TcpConnection con = new TcpConnection( this );
		
		SelectionKey key = thread.register(
				chan
				, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE
				, con
			);
		
		con.attach( key );
		key.attach( con );
		
		return con;
	}
	
	public AutoCloseable listen( int port ) throws IOException {
		
		logger.debug("Starting to listen on port {}", port );
		
		ServerSocketChannel chan = ServerSocketChannel.open();
		
		//bind to port
		chan.bind( new InetSocketAddress(port) );
		
		// non-blocking
		chan.configureBlocking(false);

		SelectionKey key = thread.register( chan, SelectionKey.OP_ACCEPT );
		
		return new RemovableKey( key );
	}
	
}
