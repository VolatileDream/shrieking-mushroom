package networking.io;

import java.io.IOException;

public interface IMessageStream {

	public enum ConnectionStatus {
		Closed,
		Open,
		Changing
	}
		
	/**
	 * Returns the last time the connection read something
	 * @return A long representation of the milliseconds since epoch, of the last successful read
	 */
	long lastReceived();
	
	/**
	 * Returns the last time the connection wrote something 
	 * @return A long representation of the milliseconds since epoch, of the last successful write
	 */
	long lastSent();
	
	ConnectionStatus getStatus();
	
	boolean isClosed();
	
	void close() throws IOException;
	
	boolean addMessage( IMessage m );
	
	/**
	 * @return Returns the number of messages in the message buffer
	 */
	int numSend();
	
	/**
	 * Immediately sends a message using this connection
	 * @param m The message to send
	 * @return returns true if successful
	 * @throws IOException if an io error occurs
	 */
	boolean express( IMessage m ) throws IOException;
	
	/**
	 * Sends all the messages that are in the message buffer
	 * @throws IOException if an IO error occurs during message sending
	 */
	void flush() throws IOException;
	
	/**
	 * Reads a message from the input
	 * @return Returns null if it couldn't read a message now, and should be checked later
	 * @throws IOException if an IO Error occurs on the channel processing the messages ( such as TCP, UDP etc )
	 */
	IMessage read() throws IOException;
	
}
