package networking.io.implementation;

import networking.io.IMessage;
import networking.io.MessageType;

public class Message implements IMessage {
	
	MessageType type;
	byte[] contents;
	
	Message(){}
	
	public Message( String str, MessageType t ){
		contents = str.getBytes();
		type = t;
	}
	
	@Override
	public byte[] getContents(){
		return contents;
	}
	
	@Override
	public MessageType getType(){
		return type;
	}
		
}
