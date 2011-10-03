package protocol.implementation;

import protocol.IMessage;

public interface MyMessage extends IMessage {
	
	public byte[] getContents();
	
}
