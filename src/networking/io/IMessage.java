package networking.io;

public interface IMessage {

	/**
	 * @return Returns the message contents
	 */
	public byte[] getContents();
	
	/**
	 * @return Returns the message type of the message
	 */
	public MessageType getType();
	
}
