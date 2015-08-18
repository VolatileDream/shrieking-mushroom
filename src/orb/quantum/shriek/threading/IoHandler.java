package orb.quantum.shriek.threading;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface IoHandler {

	/**
	 * Called when key.isAcceptable() is true.
	 * @param key
	 * @throws IOException
	 */
	public void accept( SelectionKey key ) throws IOException ;

	/**
	 * Called when key.isConnectable() is true.
	 * @param key
	 * @throws IOException
	 */
	public void connect( SelectionKey key ) throws IOException ;

	/**
	 * Called when key.isWriteable() is true.
	 * @param key
	 * @throws IOException
	 */
	public void write( SelectionKey key ) throws IOException ;

	/**
	 * Called when key.isReadable() is true.
	 * @param key
	 * @throws IOException
	 */
	public void read( SelectionKey key ) throws IOException ;

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
	public void close( SelectionKey key ) throws IOException ;
	
}
