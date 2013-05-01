package com.quantum.shriek.events;

import java.nio.ByteBuffer;


import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.quantum.shriek.tcp.TcpConnection;

public final class EventBuilder {

	private RingBuffer<Event> buffer;
	
	public EventBuilder( RingBuffer<Event> rb ){
		this.buffer = rb;
	}
	
	public void connectionCreated( TcpConnection conn ){
		long seq = buffer.next();
		Event e = buffer.get( seq );
		e.buildEvent(conn);
		buffer.publish( seq );
	}
	
	public void readCompleted( TcpConnection conn, ByteBuffer buf ){
		long seq = buffer.next();
		Event e = buffer.get( seq );
		e.buildEvent(conn, buf);
		buffer.publish( seq );
	}

	public final static EventFactory<Event> FACTORY = new EventFactory<Event>() {
		@Override
		public Event newInstance() {
			return new Event();
		}
	};
	
}
