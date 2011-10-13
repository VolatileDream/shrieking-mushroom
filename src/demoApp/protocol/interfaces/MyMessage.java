package demoApp.protocol.interfaces;

import demoApp.protocol.MessageType;
import protocol.IMessage;

public interface MyMessage extends IMessage {
	
	public byte[] getContents();
	
	public MessageType getType();
	
}
