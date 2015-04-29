package orb.quantum.shriek.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import orb.quantum.shriek.tcp.TcpThread;
import orb.quantum.shriek.threading.ChannelThread;
import orb.quantum.shriek.udp.UdpServerConnection.UdpClientConnection;
import orb.quantum.shriek.udp.UdpServerConnection.UdpWrite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UdpThread extends ChannelThread implements Runnable {

	private static final Logger logger = LogManager.getLogger( TcpThread.class );

	private UdpMushroom mushroom;

	UdpThread( UdpMushroom m, Selector s ){
		super( s );
		this.mushroom = m;
	}

	private void unsupported(){
		logger.fatal("Operation should not be triggered by UDP socket.");
		throw new RuntimeException("Operation should not be triggered by UDP socket.");
	}

	@Override
	protected void accept(SelectionKey key) throws IOException {
		unsupported();
	}

	@Override
	protected void connect(SelectionKey key) throws IOException {
		unsupported();
	}

	@Override
	protected void close(SelectionKey key) throws IOException {
		unsupported();
	}

	@Override
	protected void write(SelectionKey key) throws IOException {
		DatagramChannel chan = (DatagramChannel) key.channel();
		UdpServerConnection udp = (UdpServerConnection) key.attachment();

		UdpWrite buf = null;

		// find something to write
		while( udp.hasWrite() ){
			buf = udp.fetchWrite();
			if( buf != null ){
				break;
			}
		}

		if( buf != null ){

			if( ! buf.buffer.isReadOnly() ){
				logger.debug("Writing to {}. Data {}", chan.getRemoteAddress(), buf.buffer.array() );
			}else{
				logger.debug("Writing to {}", chan.getRemoteAddress() );
			}

			chan.send(buf.buffer, buf.address);
		}
	}

	@Override
	protected void read(SelectionKey key) throws IOException {

		DatagramChannel chan = (DatagramChannel) key.channel();
		UdpServerConnection udp = (UdpServerConnection) key.attachment();

		logger.debug("Reading from {}", chan.getRemoteAddress() );

		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.clear();

		SocketAddress addr = chan.receive(buf);

		if( buf.position() <= 0 ){
			return;
		}

		buf.flip();

		logger.trace("Reading from {}. Data {}", addr, buf.array() );

		// Then we have to create a new InternalConnection to attach
		// to this read event.
		
		UdpClientConnection conn = udp.connect(addr);
		mushroom.eventBuilder().readCompleted( conn, buf );
	}

}
