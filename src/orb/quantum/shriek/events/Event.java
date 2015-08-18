package orb.quantum.shriek.events;

import java.nio.ByteBuffer;

import orb.quantum.shriek.common.Connection;




public final class Event {

	public enum EventType { CONNECT, READ, CLOSE };

	// Connection stuff
	private Connection connection;

	// generic stuff
	private ByteBuffer buffer;
	private long time;
	private EventType event = EventType.READ;

	public Event() {}

	public long getTimestamp() {
		return time;
	}

	public EventType getEventType(){
		return event;
	}

	public Connection getConnection(){
		return connection;
	}

	public ByteBuffer getData(){
		return buffer;
	}

	void buildRead( ByteBuffer data ){
		if( data != null ){
			data = data.asReadOnlyBuffer();
		}
		buffer = data;
		event = EventType.READ;
	}

	void buildClose(){
		event = EventType.CLOSE;
	}

	void buildEvent( Connection conn ){
		time = System.currentTimeMillis();
		event = EventType.CONNECT;

		connection = conn;
	}

}
