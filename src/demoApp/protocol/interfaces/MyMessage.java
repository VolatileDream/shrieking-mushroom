package demoApp.protocol.interfaces;

import protocol.IMessage;
import demoApp.protocol.MessageType;

public interface MyMessage extends IMessage {

	public byte[] getContents();

	public MessageType getType();

}
