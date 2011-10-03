package protocol.implementation.interfaces;

import protocol.IMessage;
import protocol.implementation.MessageType;

public interface MyMessage extends IMessage {
	
	public byte[] getContents();
	
	public MessageType getType();
	
}
