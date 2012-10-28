package shriekingmushroom.tcp;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import shriekingmushroom.events.EventBuilder;

public class TcpConnection implements AutoCloseable {
	
	private static final Logger logger = LogManager.getLogger( TcpConnection.class );
	
	private TcpMushroom mushroom;
	
	private BlockingQueue<ByteBuffer> writeQueue = new LinkedBlockingQueue<ByteBuffer>();
	
	private SelectionKey key;
	
	TcpConnection( TcpMushroom m ){
		mushroom = m;
	}
	
	void attach( SelectionKey k ){
		key = k;
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
	
	@Override
	public void close() throws Exception {
		
		logger.debug("Closing TcpConnection");
		
		key.channel().close();
		key.cancel();
	}
	
	public EventBuilder eventBuilder(){
		return mushroom.eventBuilder();
	}
}
