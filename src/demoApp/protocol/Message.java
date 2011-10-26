package demoApp.protocol;

import demoApp.protocol.interfaces.MyMessage;

public class Message implements MyMessage {

	MessageType type;
	byte[] contents;

	Message() {
	}

	public Message(byte[] insides, MessageType t) {
		contents = insides;
		type = t;
	}

	@Override
	public MessageType getType() {
		return type;
	}

	@Override
	public byte[] getContents() {
		return contents;
	}

}
