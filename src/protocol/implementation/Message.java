package protocol.implementation;

import networking.IConnection;

public class Message implements MyMessage {

	private final IConnection con;
	private final byte[] contents;
	
	public Message( IConnection c, byte[] insides ){
		con = c;
		contents = insides;
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
