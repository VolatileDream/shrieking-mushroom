package orb.quantum.shriek.events;

import java.nio.ByteBuffer;

import orb.quantum.shriek.common.Connection;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;

public final class EventBuilder {

	private RingBuffer<Event> buffer;
	
	public EventBuilder( RingBuffer<Event> rb ){
		this.buffer = rb;
	}
	
	public void connectionCreated( Connection conn ){
		long seq = buffer.next();
		Event e = buffer.get( seq );
		e.buildEvent(conn);
		buffer.publish( seq );
	}
	
	public void readCompleted( Connection conn, ByteBuffer buf ){
		long seq = buffer.next();
		Event e = buffer.get( seq );
		
		e.buildEvent(conn);
		e.buildRead(buf);
		
		buffer.publish( seq );
	}

	public void connectionClose( Connection conn ){
		long seq = buffer.next();
		Event e = buffer.get( seq );
		
		e.buildEvent(conn);
		e.buildClose();
		
		buffer.publish( seq );
	}
	
	public final static EventFactory<Event> FACTORY = new EventFactory<Event>() {
		@Override
		public Event newInstance() {
			return new Event();
		}
	};
	
}
