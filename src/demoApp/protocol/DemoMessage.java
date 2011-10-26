package demoApp.protocol;

import demoApp.protocol.interfaces.DemoMyMessage;

public class DemoMessage implements DemoMyMessage {

	DemoMessageType type;
	byte[] contents;

	DemoMessage() {
	}

	public DemoMessage(byte[] insides, DemoMessageType t) {
		contents = insides;
		type = t;
	}

	@Override
	public DemoMessageType getType() {
		return type;
	}

	@Override
	public byte[] getContents() {
		return contents;
	}

}
