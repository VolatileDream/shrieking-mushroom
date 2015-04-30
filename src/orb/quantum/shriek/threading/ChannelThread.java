package orb.quantum.shriek.threading;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Set;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChannelThread implements Runnable, Stopable {

	private static final Logger logger = LogManager.getLogger( ChannelThread.class );
	
	// these objects are used to synchronize and fix the really weird synchronization
	// that is built around Selector, SelectableChannel and SelectionKey. :(
	
	// the ChannelThread object counts as the 1 that this phaser is initialized with.
	private Phaser selectorPhaser = new Phaser(1);
	private Semaphore selectorSem = new Semaphore(0);
	
	
	
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
		stop = true;
		// get the selector to wakeup.
		select.wakeup();
	}

	private void shutdownSelector(){
		int depth = shutdownSelector(0);
		logger.debug("Shutdown ChannelThread: {}", depth);
	}
	
	public SelectionKey register( AbstractSelectableChannel chan, int ops ) throws ClosedChannelException {
		return register(chan, ops, null);
	}
	
	public SelectionKey register( AbstractSelectableChannel chan, int ops, Object attach ) throws ClosedChannelException {
		SelectionKey key;
		
		synchronized(selectorPhaser){
			System.out.println("register: wakeup");
			select.wakeup();
			
			System.out.println("register: phaser");
			// register and arrive for this phase.
			selectorPhaser.register();
			selectorPhaser.awaitAdvance( selectorPhaser.arrive() );
			System.out.println("register: after phaser");
			
			// -------
			// At this point another thread will get scheduled.
			// It may or may not be another registering thread.
			// -------
			
			System.out.println("register: selector ready!");
			key = chan.register(select, ops, attach);
			
			// arrive for the next phase, and remove ourselves
			selectorPhaser.arriveAndDeregister();
			System.out.println("register: no more phaser");
			
			// release resources for the selector thread to advance.
			selectorSem.release();
			System.out.println("register: done");
		}
		
		return key;
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

	private final void loop() throws Exception {

		// Have a time out so that registering new keys doesn't block forever.
		int count = select.select(10);
		
		if (count == 0){
			// We got woken up because someone has attempted to register a new
			// key to our selector. This is to fix up the weird behaviour around
			// synchronization with Selector.
			System.out.println("Selector: awake");
			synchronized(selectorPhaser){
				System.out.println("Selector: locked");
				// this call does NOT release the lock on selectorPhaser.
				// Which is a critical design point for this synchro mechanism.
				selectorPhaser.arrive();
				
				System.out.println("Selector: arrived");
				
				// -1 : we don't wait for ourselves (this thread is registered)
				int waitCount = selectorPhaser.getRegisteredParties() - 1;
				
				// Any thread that made it to registering for the Phaser is
				// going to release the semaphore, once they all have then they
				// have all registered their channels with the Selector. And
				// then we can resume.
				selectorSem.acquire(waitCount);
				
				System.out.println("Selector: acquired");
			}
			
			return; // nothing to do, return.
		}

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
