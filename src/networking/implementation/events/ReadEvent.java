package networking.implementation.events;

import networking.IConnection;
import networking.events.IReadEvent;

public class ReadEvent extends NetworkEvent implements IReadEvent {

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
