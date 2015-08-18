package orb.quantum.shriek.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import orb.quantum.shriek.threading.ChannelThread.KeyAttachment;
import orb.quantum.shriek.threading.IoHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpHandler implements IoHandler {

	private static final Logger logger = LogManager.getLogger( TcpHandler.class );
	
	private final TcpMushroom mushroom;
	
	TcpHandler( TcpMushroom m ){
		this.mushroom = m;
	}
	
	private TcpConnection getConnection(SelectionKey key){
		return (TcpConnection) ((KeyAttachment) key.attachment()).connection;
	}
	
	public void accept( SelectionKey key ) throws IOException {
		
		ServerSocketChannel serv = (ServerSocketChannel) key.channel();
		
		if(!serv.isOpen()){
			return;
		} else {
			logger.debug("Accepting Socket connection: {}", serv.getLocalAddress() );
		}
		
		
		SocketChannel chan = null;
		try {
			chan = serv.accept();
		} catch (AsynchronousCloseException e) {
			// this happens, it's the same as erroneously attempting to accept
		}
		
		if( chan != null ){
			logger.debug("Accepted Socket connection from {}", chan.getRemoteAddress() );
			
			TcpConnection tcp = mushroom.createTcpConnection(chan);
			
			mushroom.builder.connectionCreated( tcp );
		}
	}
	
	public void connect( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = getConnection(key);

		logger.debug("Finising connection to {}", chan.getRemoteAddress() );
		
		if( chan.finishConnect() ){
			mushroom.builder.connectionCreated( tcp );
		}
	}
	
	public void write( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = getConnection(key);
		
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
		TcpConnection tcp = getConnection(key);
		
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
		TcpConnection tcp = getConnection(key);

		// server keys don't have a stop listening event. They can also be
		// closed multiple ways, and they do _not_ have a connection. 
		
		if( tcp != null ){

			mushroom.builder.connectionClose(tcp);
		}
	}
	
}
