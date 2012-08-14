package shriekingmushroom.events;

import shriekingmushroom.tcp.TcpConnection;


public abstract class Event {

	private final TcpConnection connection;
	private final long time;

	public Event( TcpConnection con ) {
		time = System.currentTimeMillis();
		connection = con;
	}

	public long getTimestamp() {
		return time;
	}

	public TcpConnection getConnection(){
		return connection;
	}
}
