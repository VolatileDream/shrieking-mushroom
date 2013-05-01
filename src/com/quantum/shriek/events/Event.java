package com.quantum.shriek.events;

import java.nio.ByteBuffer;

import com.quantum.shriek.tcp.TcpConnection;



public final class Event {

	public enum Type { TCP, UDP, MULTI };
	
	private TcpConnection connection;
	private ByteBuffer buffer;
	private long time;

	public Event() {}

	public long getTimestamp() {
		return time;
	}
	
	public TcpConnection getConnection(){
		return connection;
	}
	
	public ByteBuffer getData(){
		return buffer;
	}
	
	void buildEvent( TcpConnection conn, ByteBuffer data ){
		time = System.currentTimeMillis();
		connection = conn;
		if( data != null ){
			data = data.asReadOnlyBuffer();
		}
		buffer = data;
	}
	
	void buildEvent( TcpConnection conn ){
		buildEvent( conn, null );
	}
}
