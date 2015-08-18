package orb.quantum.shriek.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import orb.quantum.shriek.threading.IoHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpHandler implements IoHandler {

	private static final Logger logger = LogManager.getLogger( TcpHandler.class );
	
	private final TcpMushroom mushroom;
	
	TcpHandler( TcpMushroom m ){
		this.mushroom = m;
	}
		
	public void accept( SelectionKey key ) throws IOException {
		
		ServerSocketChannel serv = (ServerSocketChannel) key.channel();
		
		logger.debug("Accepting Socket connection: {}", serv.getLocalAddress() );
		
		SocketChannel chan = serv.accept();
		
		logger.debug("Accepted Socket connection from {}", chan.getRemoteAddress() );
		
		if( chan != null ){
			TcpConnection tcp = mushroom.createTcpConnection(chan);
			
			mushroom.builder.connectionCreated( tcp );
		}
	}
	
	public void connect( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = (TcpConnection) key.attachment();

		logger.debug("Finising connection to {}", chan.getRemoteAddress() );
		
		if( chan.finishConnect() ){
			mushroom.builder.connectionCreated( tcp );
		}
	}
	
	public void write( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = (TcpConnection) key.attachment();
		
		ByteBuffer buf = null;
		
		// find something to write
		while( tcp.hasWrite() ){
			buf = tcp.fetchWrite();
			if( buf != null && buf.hasRemaining() ){
				break;
			}
		}
		
		if( buf != null ){
					
			if( ! buf.isReadOnly() ){
				logger.debug("Writing to {}. Data {}", chan.getRemoteAddress(), buf.array() );
			}else{
				logger.debug("Writing to {}", chan.getRemoteAddress() );
			}
			
			chan.write( buf );
		}
	}

	public void read( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = (TcpConnection) key.attachment();
		
		logger.debug("Reading from {}", chan.getRemoteAddress() );
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.clear();
		
		int read = chan.read(buf);
		
		if( read <= 0 ){
			return;
		}
		
		buf.flip();

		logger.trace("Reading from {}. Data {}", chan.getRemoteAddress(), buf.array() );
		
		mushroom.builder.readCompleted( tcp, buf );
	}

	@Override
	public void close(SelectionKey key) throws IOException {
		Object attach = key.attachment();

		// server keys don't have a stop listening event. They can also be
		// closed multiple ways, and they do _not_ have an attachment. 
		
		if( attach != null && attach instanceof TcpConnection ){
		
			TcpConnection tcp = (TcpConnection) attach;
					
			mushroom.builder.connectionClose(tcp);
		}
	}
	
}
