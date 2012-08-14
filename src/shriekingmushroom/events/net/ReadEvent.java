package shriekingmushroom.events.net;

import java.nio.ByteBuffer;

import shriekingmushroom.events.Event;
import shriekingmushroom.tcp.TcpConnection;

public class ReadEvent extends Event {

	private ByteBuffer buffer;
	
	public ReadEvent( TcpConnection con, ByteBuffer buf ) {
		super(con);
		buffer = buf.asReadOnlyBuffer();
	}

	public ByteBuffer getRead(){
		return buffer;
	}
	
}
