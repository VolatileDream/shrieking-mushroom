package shriekingMushroom.networking.implementation.events;

import shriekingMushroom.networking.IConnection;
import shriekingMushroom.networking.events.INetReadEvent;

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
