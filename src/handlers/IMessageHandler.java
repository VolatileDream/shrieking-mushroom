package handlers;

import networking.io.IMessage;
import networking.io.MessageType;

public interface IMessageHandler {
	
	public void handle( IMessage m );
	
	public MessageType[] validTypes();
	
}
