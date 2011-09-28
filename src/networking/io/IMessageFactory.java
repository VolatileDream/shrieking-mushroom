package networking.io;

import core.Tupple;
import networking.exceptions.InsufficientMessageLengthException;
import networking.exceptions.MalformedMessageException;

/**
 * This class allows for IMessage conversion to and from byte[].
 * <br><br>
 * Calling .toBytes( .fromBytes( byte[] ) ) will produce an identical
 * byte array to the one passed in.
 * 
 * @author jex
 *
 */
public interface IMessageFactory {

	/**
	 * Converts a message to a byte array
	 * @return Returns an array of bytes that represent the message
	 */
	public byte[] toBytes( IMessage m );
	
	/**
	 * Attempts to translate a byte array into an IMessage, throws exceptions if unsuccessful
	 * @param array Byte array to be parsed into a message
	 * @param isTorturedStream Boolean value indicating if that there may not be a valid 'end' to the last message
	 * @throws MalformedMessageException If the message is misformed
	 * @throws InsufficientMessageLengthException if the byte array isn't long enough to store even the shortest message.
	 * @return Returns a tupple of a Message and the number of bytes read from the array passed in
	 */
	public Tupple<IMessage,Integer> fromBytes( byte[] array, boolean isTorturedStream ) throws MalformedMessageException, InsufficientMessageLengthException;
	
	/**
	 * Returns the first index that is a possible start of a message, or -1 if there is no possible start.
	 * @param contents the buffer to check
	 * @param isTorturedStream If the buffer or input has been "tortured" and may not have the end of the previous message
	 * @return Returns the first index that could possibly be the start of a message or -1 if there is no possible start
	 */
	public int getFirstValidMessageStart( byte[] contents, boolean isTorturedStream );
	
}
