package orb.quantum.shriek.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import orb.quantum.shriek.common.Connection;
import orb.quantum.shriek.events.EventBuilder;
import orb.quantum.shriek.tcp.TcpMushroom;
import orb.quantum.shriek.threading.Stopable;
import orb.quantum.shriek.udp.UdpServerConnection.UdpClientConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UdpMushroom implements Stopable {
	
	private static final Logger logger = LogManager.getLogger( TcpMushroom.class );
	
	private final Selector selectClient;
	private final EventBuilder builder;
		
	private UdpThread client;
	private UdpServerConnection wildcard; // for any non specified connection
	
	public UdpMushroom( EventBuilder builder ) throws IOException {
		selectClient = Selector.open();
		this.builder = builder;
	}
	
	private UdpThread startThread( Selector select ){
		
		logger.debug("Creating TcpThread");
		
		UdpThread udp = new UdpThread(this, select);
		
		Thread th = new Thread( udp );
		th.start();
		
		return udp;
	}
	
	public Connection connect( UdpServerConnection conn, InetAddress addr, int port ) throws IOException{
		return conn.connect(new InetSocketAddress(addr, port));
	}
	
	public UdpClientConnection connect( InetAddress addr, int port ) throws IOException {
		
		logger.debug("Connecting to {}:{}", addr, port );
		
		synchronized(this){
			if( wildcard == null ){
				DatagramChannel chan = DatagramChannel.open();
				wildcard = createUdpConnection(chan);
			}
		}
		
		return wildcard.connect(new InetSocketAddress(addr, port));
	}
	
	public UdpServerConnection listen( int port ) throws IOException {
		
		logger.debug("Starting to listen on port {}", port );
		
		DatagramChannel chan = DatagramChannel.open();
		
		//bind to local port
		chan.bind( new InetSocketAddress(port) );
		
		UdpServerConnection conn = createUdpConnection(chan);
		
		return conn;
	}
	
	UdpServerConnection createUdpConnection( DatagramChannel chan ) throws IOException {
		
		synchronized (this) {
			// create the client on demand
			if (client == null) {
				client = startThread(selectClient);
			}
		}
		chan.configureBlocking(false);
		
		UdpServerConnection con = new UdpServerConnection( this.eventBuilder() );
		
		//bump the selector, required to make sure the channel registration doesn't block
		selectClient.wakeup();
		
		SelectionKey key =
				chan.register(
					selectClient,
					SelectionKey.OP_READ | SelectionKey.OP_WRITE,
					con
				);
		con.attach( key );
		key.attach( con );
		
		return con;
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
		}
	}
	
}
