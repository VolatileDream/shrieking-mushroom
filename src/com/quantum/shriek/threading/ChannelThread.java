package com.quantum.shriek.threading;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChannelThread implements Runnable, Stopable {

	private static final Logger logger = LogManager.getLogger( ChannelThread.class );
	
	private Selector select;
	
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

	public final void stop(){
		stop = false;
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
				close(key);
				
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

	private final void loop() throws Exception{

		select.select();

		for( SelectionKey key : select.selectedKeys() ){

			// dealing with canceled keys
			if( ! key.isValid() ){
				continue;
			}
			
			if( key.isAcceptable() ){
				accept(key);
				// keys that are acceptable will not be any of the other things
				continue;
			}

			if( key.isConnectable() ){
				connect(key);
			}

			if( key.isWritable() ){
				write(key);
			}

			if( key.isReadable() ){
				read(key);
			}

		}

	}

	/**
	 * Called when key.isAcceptable() is true.
	 * @param key
	 * @throws IOException
	 */
	protected abstract void accept( SelectionKey key ) throws IOException ;

	/**
	 * Called when key.isConnectable() is true.
	 * @param key
	 * @throws IOException
	 */
	protected abstract void connect( SelectionKey key ) throws IOException ;

	/**
	 * Called when key.isWriteable() is true.
	 * @param key
	 * @throws IOException
	 */
	protected abstract void write( SelectionKey key ) throws IOException ;

	/**
	 * Called when key.isReadable() is true.
	 * @param key
	 * @throws IOException
	 */
	protected abstract void read( SelectionKey key ) throws IOException ;

	/**
	 * Allows a base class to do any custom clean up of the connection.
	 * The closing of the Channel and SelectionKey are done by the
	 * super class automatically.
	 * <br>
	 * None of the other abstract functions (accept, connect, write, read)
	 * will ever be invoked after a call to this function. 
	 * @param key
	 * @throws IOException
	 */
	protected abstract void close( SelectionKey key ) throws IOException ;
	
}
