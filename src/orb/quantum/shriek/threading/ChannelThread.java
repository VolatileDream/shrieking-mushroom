package orb.quantum.shriek.threading;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import orb.quantum.shriek.common.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChannelThread implements Runnable, Stopable {

	public static class KeyAttachment {

		public Connection connection;
		public final IoHandler handler;

		public KeyAttachment( Connection c, IoHandler h){
			if(h == null){
				throw new IllegalArgumentException("IoHandler can't be null");
			}
			connection = c;
			handler = h;
		}

	}

	private static final Logger logger = LogManager.getLogger( ChannelThread.class );

	// Concurrency is hard. :c
	final ReentrantLock selectorLock = new ReentrantLock();

	private final Selector select;

	private volatile boolean stop = false;

	public ChannelThread( Selector s ){
		this.select = s;
	}

	@Override
	public final void run() {

		while( ! stop ) {

			try {
				loop();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}

		}

		shutdownSelector();
	}

	@Override
	public final void stop(){
		stop = true;
		// get the selector to wakeup.
		select.wakeup();
	}

	private void shutdownSelector(){
		int depth = shutdownSelector(0);
		logger.debug("Shutdown ChannelThread: {}", depth);
	}

	private int shutdownSelector( int recursionDepth ){

		try {
			Set<SelectionKey> keys = select.keys();

			for( SelectionKey key : keys ){
			
				try {
					closeKey(key);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			select.close();

		} catch (ClosedSelectorException e) {

			//already closed, nothing that we can do
			// although it shouldn't be closed...

			logger.debug(e);

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);

			//try 
			return shutdownSelector( recursionDepth + 1 );
		}

		return recursionDepth;
	}

	private void closeKey(SelectionKey key) throws IOException{
		// custom close things
		KeyAttachment attach = (KeyAttachment) key.attachment();
		IoHandler handler = attach.handler;

		handler.close(key);

		key.channel().close();
		key.cancel();
	}
	
	public SelectionKey register( AbstractSelectableChannel chan, int ops, Connection con, IoHandler handler ) {
		return register( chan, ops, new KeyAttachment(con, handler) );
	}

	public SelectionKey register( final AbstractSelectableChannel chan, final int ops, final KeyAttachment attach ) {

		if( attach == null ){
			throw new IllegalArgumentException("KeyAttachment can't be null.");
		}

		try {
			selectorLock.lock();
			select.wakeup();
			return chan.register(select, ops, attach);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
			logger.error(e);
		} finally {
			selectorLock.unlock();
		}
		return null;
	}

	private final void loop() throws IOException {

		// yes, this looks silly, but it allows registration to complete.
		// from: http://stackoverflow.com/a/1112809/296828
		selectorLock.lock();
		selectorLock.unlock();

		select.select();

		for( SelectionKey key : select.selectedKeys() ){

			try {
				handleKey(key);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					closeKey(key);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}

	}

	private void handleKey(SelectionKey key) throws IOException{
		// dealing with canceled keys
		if( ! key.isValid() ){
			return;
		}

		KeyAttachment attach = ((KeyAttachment) key.attachment());
		IoHandler handler = attach.handler;

		if( key.isAcceptable() ){
			handler.accept(key);
			// keys that are acceptable will not be any of the other things
			return;
		}

		if( key.isConnectable() ){
			handler.connect(key);
		}

		if( key.isWritable() ){
			handler.write(key);
		}

		if( key.isReadable() ){
			handler.read(key);
		}
	}


}
