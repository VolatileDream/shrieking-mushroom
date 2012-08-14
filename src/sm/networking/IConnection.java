package sm.networking;

import java.io.IOException;
import java.net.InetAddress;

import sm.networking.exceptions.ConnectionClosedException;


public interface IConnection {

	/**
	 * Returns the address of the remote machine ( or the multicast group
	 * address ( in the case of multicast ) )
	 */
	public InetAddress getAddress();

	/**
	 * Returns the port on the remote machine to which this is connected
	 */
	public int getPort();

	/**
	 * Queues an asynchronous write event to the connection. <br>
	 * Each call to write will produce a single call to the network layer,
	 * except in the case of TCP where multiple calls may be required to flush
	 * all the data.
	 * 
	 * @param m
	 *            The byte[] to write to the connection
	 * @return Returns true if successfully queued or false if not.
	 * @throws ConnectionClosedException
	 *             If the connection has already been closed
	 */
	boolean write(byte[] m) throws ConnectionClosedException;

	/**
	 * Returns the last time the connection read something
	 * 
	 * @return A long representation of the milliseconds since epoch, of the
	 *         last successful read
	 */
	long lastReceived();

	/**
	 * Returns the last time the connection wrote something
	 * 
	 * @return A long representation of the milliseconds since epoch, of the
	 *         last successful write
	 */
	long lastSent();

	/**
	 * Flushes all queued output, and then closes the connection.
	 */
	void close() throws IOException;

	/**
	 * Returns true if the connection is closed
	 * @return
	 */
	boolean isClosed();
}
