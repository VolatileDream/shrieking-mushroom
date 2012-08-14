package sm.networking.implementation.events;

import sm.networking.IConnection;
import sm.networking.events.INetReadEvent;

public class ReadEvent extends NetworkEvent implements INetReadEvent {

	private final byte[] read;

	public ReadEvent(IConnection c, byte[] r) {
		super(c);
		read = r;
	}

	@Override
	public byte[] getRead() {
		return read;
	}

}
