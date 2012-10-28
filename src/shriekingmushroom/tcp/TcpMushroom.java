package shriekingmushroom.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import shriekingmushroom.events.EventBuilder;
import shriekingmushroom.threading.IStopper;

public class TcpMushroom {
	
	private static final Logger logger = LogManager.getLogger( TcpMushroom.class );
	
	private Selector selectServer;
	private Selector selectClient;
	private IStopper stopper;
	private EventBuilder builder;
	
	private TcpThread server;
	private TcpThread client;
	
	public TcpMushroom( IStopper stop, EventBuilder builder ) throws IOException {
		selectServer = Selector.open();
		selectClient = Selector.open();
		stopper = stop;
		this.builder = builder;
	}
	
	private  TcpThread startThread( Selector select ){
		
		logger.debug("Creating TcpThread");
		
		TcpThread tcp = new TcpThread(this, select, stopper);
		
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
		
		// create the client on demand
		if( client == null ){
			client = startThread(selectClient);
		}
		
		chan.configureBlocking(false);
		
		TcpConnection con = new TcpConnection( this );
		
		//bump the selector, required to make sure the channel registration doesn't block
		selectClient.wakeup();
		
		SelectionKey key =
				chan.register(
					selectClient,
					SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE,
					con
				);
		con.attach( key );
		key.attach( con );
		
		return con;
	}
	
	public AutoCloseable listen( int port ) throws IOException {
		
		logger.debug("Starting to listen on port {}", port );
		
		// create server on demand
		if( server == null ){
			server = startThread(selectServer);
		}
		
		ServerSocketChannel chan = ServerSocketChannel.open();
		
		//bind to port
		chan.bind( new InetSocketAddress(port) );
		
		// non-blocking
		chan.configureBlocking(false);
		
		//bump the selector, required to make sure the channel registration doesn't block
		selectServer.wakeup();
		
		SelectionKey key = chan.register( selectServer, SelectionKey.OP_ACCEPT );
		
		return new RemovableKey( key );
	}
	
	public EventBuilder eventBuilder(){
		return builder;
	}
	
}
