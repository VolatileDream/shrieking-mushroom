package shriekingmushroom.tcp;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import shriekingmushroom.events.Event;

public class TcpConnection implements AutoCloseable {
	
	private TcpMushroom mushroom;
	
	private BlockingQueue<ByteBuffer> writeQueue = new LinkedBlockingQueue<ByteBuffer>();
	
	private SelectionKey key;
	
	TcpConnection( TcpMushroom m, SelectionKey k ){
		mushroom = m;
		key = k;
	}
	
	/**
	 * 
	 * @param buf
	 * @return
	 * @throws ClosedChannelException
	 */
	public boolean write( ByteBuffer buf ) throws ClosedChannelException {
		
		if( ! key.channel().isOpen() ){
			throw new ClosedChannelException();
		}
		return writeQueue.add(buf);
	}

	boolean hasWrite(){
		return ! writeQueue.isEmpty();
	}
	
	ByteBuffer fetchWrite(){
		return writeQueue.poll();
	}
	
	@Override
	public void close() throws Exception {
		
		key.channel().close();
		
		key.cancel();
		
	}
	
	public BlockingQueue<Event> eventQueue(){
		return mushroom.eventQueue();
	}
}
