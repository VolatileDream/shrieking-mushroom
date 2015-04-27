package orb.quantum.shriek.events;

import java.nio.ByteBuffer;

import orb.quantum.shriek.common.Connection;
import orb.quantum.shriek.multicast.MulticastConnection;
import orb.quantum.shriek.tcp.TcpConnection;
import orb.quantum.shriek.udp.UdpConnection;




public final class Event {

	public enum ConnectionType { TCP, UDP, MULTICAST };

	public enum EventType { CONNECT, READ, CLOSE };

	// Connection stuff
	private Connection connection;

	// generic stuff
	private ByteBuffer buffer;
	private long time;
	private EventType event = EventType.READ;
	private ConnectionType type = ConnectionType.TCP;

	public Event() {}

	public long getTimestamp() {
		return time;
	}

	public EventType getEventType(){
		return event;
	}

	public ConnectionType getConnectionType(){
		return type;
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
		type = getType(conn);
	}
	
	private ConnectionType getType( Connection c ){
		if( c instanceof TcpConnection ){
			return ConnectionType.TCP;
		} else if ( c instanceof UdpConnection ){
			return ConnectionType.UDP;
		} else if ( c instanceof MulticastConnection ){
			return ConnectionType.MULTICAST;
		} else {
			throw new RuntimeException("Unknown connection type.");
		}
	}

}
