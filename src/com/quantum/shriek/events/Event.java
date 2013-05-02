package com.quantum.shriek.events;

import java.nio.ByteBuffer;

import com.quantum.shriek.tcp.TcpConnection;



public final class Event {

	public enum ConnectionType { TCP, UDP, MULTI };
	
	public enum EventType { CONNECT, READ, CLOSE };
	
	// Connection stuff
	private TcpConnection tcpConnection;
	
	// generic stuff
	private ByteBuffer buffer;
	private long time;
	private EventType event = EventType.READ;
	private ConnectionType connection = ConnectionType.TCP;

	public Event() {}

	public long getTimestamp() {
		return time;
	}
	
	public EventType getEventType(){
		return event;
	}
	
	public ConnectionType getConnectionType(){
		return connection;
	}
	
	public TcpConnection getConnection(){
		return tcpConnection;
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
	
	void buildEvent( TcpConnection conn ){
		time = System.currentTimeMillis();
		tcpConnection = conn;
		
		event = EventType.CONNECT;
		connection = ConnectionType.TCP;
	}
}
