package orb.quantum.shriek.threading;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Set;

import orb.quantum.shriek.common.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChannelThread implements Runnable, Stopable {

	private static final Logger logger = LogManager.getLogger( ChannelThread.class );

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
				logger.debug(e);
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
				// custom close things
				IoHandler handler = ((Connection) key.attachment()).getHandler();
				
				handler.close(key);

				key.channel().close();
				key.cancel();
			}

			select.close();

		} catch (ClosedSelectorException e) {

			//already closed, nothing that we can do
			// although it shouldn't be closed...

			logger.debug(e);

		} catch (IOException e) {
			logger.debug(e);

			//try 
			return shutdownSelector( recursionDepth + 1 );
		}

		return recursionDepth;
	}
	
	public SelectionKey register( AbstractSelectableChannel chan, int ops ) {
		return register(chan, ops, null);
	}

	public SelectionKey register( final AbstractSelectableChannel chan, final int ops, final Object attach ) {
		
		select.wakeup();
		try {
			return chan.register(select, ops, attach);
		} catch (ClosedChannelException e) {
			logger.error(e);
		}
		return null;
	}

	private final void loop() throws Exception {
	
		// Have a time out so that registering new keys doesn't block forever.
		select.select();

		for( SelectionKey key : select.selectedKeys() ){

			// dealing with canceled keys
			if( ! key.isValid() ){
				continue;
			}

			IoHandler handler = ((Connection) key.attachment()).getHandler();
			
			if( key.isAcceptable() ){
				handler.accept(key);
				// keys that are acceptable will not be any of the other things
				continue;
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



}
