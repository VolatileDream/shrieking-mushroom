package events.implementation;

import networking.interfaces.IConnection;
import events.IReadEvent;

public class ReadEvent extends Event implements IReadEvent {

	private final byte[] read;
	
	public ReadEvent( IConnection c, byte[] r ){
		super( c );
		read = r;
	}
	
	@Override
	public byte[] getRead() {
		return read;
	}

}
