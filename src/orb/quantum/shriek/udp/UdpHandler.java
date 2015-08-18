package orb.quantum.shriek.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import orb.quantum.shriek.tcp.TcpHandler;
import orb.quantum.shriek.threading.IoHandler;
import orb.quantum.shriek.udp.UdpServerConnection.UdpClientConnection;
import orb.quantum.shriek.udp.UdpServerConnection.UdpWrite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UdpHandler implements IoHandler {

	private static final Logger logger = LogManager.getLogger( TcpHandler.class );

	private UdpMushroom mushroom;

	UdpHandler( UdpMushroom m ){
		this.mushroom = m;
	}

	private void unsupported(){
		logger.fatal("Operation should not be triggered by UDP socket.");
		throw new RuntimeException("Operation should not be triggered by UDP socket.");
	}

	@Override
	public void accept(SelectionKey key) throws IOException {
		unsupported();
	}

	@Override
	public void connect(SelectionKey key) throws IOException {
		unsupported();
	}

	@Override
	public void close(SelectionKey key) throws IOException {
		unsupported();
	}

	@Override
	public void write(SelectionKey key) throws IOException {
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
	public void read(SelectionKey key) throws IOException {

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
