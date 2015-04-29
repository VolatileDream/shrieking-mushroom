package orb.quantum.shriek.tcp;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import orb.quantum.shriek.common.Connection;
import orb.quantum.shriek.events.EventBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TcpConnection implements Connection {
	
	private static final Logger logger = LogManager.getLogger( TcpConnection.class );
	
	protected final EventBuilder builder;
	
	private BlockingQueue<ByteBuffer> writeQueue = new LinkedBlockingQueue<ByteBuffer>();
	
	private SelectionKey key;
	
	public TcpConnection( EventBuilder b ){
		builder = b;
	}
	
	public ConnectionType getType(){
		return ConnectionType.TCP;
	}
	
	void attach( SelectionKey k ){
		key = k;
	}
	
	SelectionKey getKey(){
		return key;
	}
	
	/**
	 * 
	 * @param buf
	 * @return
	 * @throws ClosedChannelException
	 */
	public boolean write( ByteBuffer buf ) throws ClosedChannelException {
		
		logger.info("Writing");
		
		if( ! key.channel().isOpen() ){
			throw new ClosedChannelException();
		}
		return writeQueue.offer(buf);
	}

	boolean hasWrite(){
		return ! writeQueue.isEmpty();
	}
	
	ByteBuffer fetchWrite(){
		return writeQueue.poll();
	}
	
	public void close() throws Exception {
		
		logger.debug("Closing Connection");
		
		builder.connectionClose(this);
		
		key.channel().close();
		key.cancel();
	}
	
}
