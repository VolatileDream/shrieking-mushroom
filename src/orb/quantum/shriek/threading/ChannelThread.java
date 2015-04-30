package orb.quantum.shriek.threading;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChannelThread implements Runnable, Stopable {

	private static final Logger logger = LogManager.getLogger( ChannelThread.class );

	// these objects are used to synchronize and fix the really weird synchronization
	// that is built around Selector, SelectableChannel and SelectionKey. :(

	private BlockingQueue<Registration> registrations = new LinkedBlockingQueue<>();
	

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

	private class Registration implements Future<SelectionKey>, Callable<SelectionKey> {
		
		final AbstractSelectableChannel channel;
		final int operations;
		final Object attached;
		
		final FutureTask<SelectionKey> task;
		
		public Registration( AbstractSelectableChannel chan, int ops, Object attach ){
			channel = chan;
			operations = ops;
			attached = attach;
			task = new FutureTask<>(this);
		}
		@Override
		public SelectionKey call() throws Exception {
			return channel.register(ChannelThread.this.select, operations, attached);
		}
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return task.cancel(mayInterruptIfRunning);
		}
		@Override
		public boolean isCancelled() {
			return task.isCancelled();
		}
		@Override
		public boolean isDone() {
			return task.isDone();
		}
		@Override
		public SelectionKey get() throws InterruptedException, ExecutionException {
			return task.get();
		}
		@Override
		public SelectionKey get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
				TimeoutException {
			return task.get(timeout, unit);
		}
	}
	
	public SelectionKey register( AbstractSelectableChannel chan, int ops ) {
		return register(chan, ops, null);
	}

	public SelectionKey register( final AbstractSelectableChannel chan, final int ops, final Object attach ) {
		Registration newRegistration;
		synchronized(registrations){
			select.wakeup();

			newRegistration = new Registration(chan, ops, attach);
			
			registrations.add(newRegistration);
		}
		
		try {
			// both of the exceptions that come off of this suck.
			// We just rethrow them because there's not much else to do here.
			
			// For clarity: Interrupted + Execution Exception are thrown.
			return newRegistration.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final void loop() throws Exception {
		
		// Have a time out so that registering new keys doesn't block forever.
		int count = select.select();

		if (count == 0){
			// We got woken up because someone has attempted to register a new
			// key to our selector. This is to fix up the weird behaviour around
			// synchronization with Selector.

			synchronized(registrations){
				while( !registrations.isEmpty() ){
					Registration reg = registrations.remove();
					reg.task.run();
				}
			}
			
			return; // no keys to look at.
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
