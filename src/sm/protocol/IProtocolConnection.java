package sm.protocol;

import java.net.InetAddress;

import sm.networking.exceptions.ConnectionClosedException;


public interface IProtocolConnection<M extends IMessage> {

	/**
	 * Gets the remote address to which the connection is attached ( note for
	 * multicast: returns the group )
	 */
	public InetAddress getAddress();

	/**
	 * Returns the remote port to which the connection is attached
	 * 
	 * @return
	 */
	public int getPort();

	/**
	 * Returns the time in milliseconds since the epoch of when the last message
	 * was received.
	 */
	public long lastReceived();

	/**
	 * Returns the time in milliseconds since the epoch of when the last message
	 * was sent
	 */
	public long lastSent();

	/**
	 * Queues the message for writing to this connection.
	 * 
	 * @param m
	 *            The message to write.
	 * @return Returns true if the message was properly queued.
	 * @throws ConnectionClosedException
	 *             If the connection was already closed
	 */
	public boolean write(M m) throws ConnectionClosedException;

	/**
	 * Flushes all the Messages that have to be written, and then closes the connection.
	 */
	public void close();
	
	/**
	 * Returns the closed status of this connection
	 * @return Returns true if this connection is closed, and won't accept more messages.
	 */
	public boolean isClosed();
}
