package demoApp.protocol.interfaces;

import shriekingMushroom.protocol.IMessage;
import demoApp.protocol.DemoMessageType;

public interface DemoMyMessage extends IMessage {

	public byte[] getContents();

	public DemoMessageType getType();

}
