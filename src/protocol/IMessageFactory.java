package protocol;

import core.Tupple;

public interface IMessageFactory<M extends IMessage> {
	
	/**
	 * Attempts to parse a message from an array of bytes.
	 * @param b The byte array to try to parse.
	 * @return Returns a tupple with the parsed message ( null if not possible )
	 * and the number of bytes read from the array ( > 0 if a message was parsed )
	 */
	public Tupple<M,Integer> transformToMessage( byte[] b );
	
	/**
	 * Turns a message into an array of bytes for writing to IO
	 * @param msg The message to format
	 * @return Returns a byte array that represents the message 
	 */
	public byte[] transformToBytes( M msg );
	
}
