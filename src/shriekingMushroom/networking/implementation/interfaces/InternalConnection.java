package shriekingMushroom.networking.implementation.interfaces;

import java.io.IOException;

import shriekingMushroom.networking.IConnection;


public interface InternalConnection extends IConnection {

	public enum ConnectionStatus {
		Open, Closed, Changing
	};

	int numSend();

	/**
	 * Sends all the messages that are in the message buffer
	 * 
	 * @throws IOException
	 *             if an IO error occurs during message sending
	 */
	void flush() throws IOException;

	/**
	 * Read bytes in from the connection
	 * @return Returns a byte[] of the bytes read.
	 * <br>Returns a byte[0] if nothing is read.
	 * @throws IOException if an IOException occurs on the underlying io
	 */
	byte[] read() throws IOException;

}
