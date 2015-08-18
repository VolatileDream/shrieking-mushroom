package orb.quantum.shriek.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import orb.quantum.shriek.common.Connection;
import orb.quantum.shriek.events.EventBuilder;
import orb.quantum.shriek.threading.ChannelThread;
import orb.quantum.shriek.threading.Stopable;
import orb.quantum.shriek.udp.UdpServerConnection.UdpClientConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UdpMushroom implements Stopable {
	
	private static final Logger logger = LogManager.getLogger( UdpMushroom.class );
	
	final EventBuilder builder;
	final UdpHandler client;
	final ChannelThread thread;
	
	private UdpServerConnection wildcard; // for any non specified connection
	
	public UdpMushroom( EventBuilder builder, ChannelThread thread ) throws IOException {
		this.builder = builder;
		this.thread = thread;
		client = new UdpHandler(this);
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
		
		chan.configureBlocking(false);
		
		UdpServerConnection con = new UdpServerConnection( this );
		
		SelectionKey key = thread.register(chan,
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
			try {
				wildcard.close();
			} catch (Exception e) {
				logger.debug(e);
			}
			wildcard = null;
		}
	}
	
}
