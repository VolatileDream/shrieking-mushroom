package protocol.implementation;

import protocol.implementation.interfaces.MyMessage;
import networking.IConnection;

public class Message implements MyMessage {

	public final IConnection con;
	MessageType type;
	byte[] contents;
	
	public Message( IConnection c ){
		con = c;
	}
	
	public Message( IConnection c, byte[] insides, MessageType t ){
		con = c;
		contents = insides;
		type = t;
	}
	
	@Override
	public MessageType getType(){
		return type;
	}
	
	@Override
	public IConnection getConnection() {
		return con;
	}

	@Override
	public byte[] getContents() {
		return contents;
	}

}
