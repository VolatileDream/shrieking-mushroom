package networking.implementation;

import java.io.IOException;

import networking.IConnection;

public interface InternalConnection extends IConnection {

	public enum ConnectionStatus {
		Open,
		Closed,
		Changing
	};
	
	int numSend();
	
	/**
	 * Sends all the messages that are in the message buffer
	 * @throws IOException if an IO error occurs during message sending
	 */
	void flush() throws IOException;
	
	byte[] read() throws IOException;
	
	void close() throws IOException;
	
	boolean isClosed();
}
