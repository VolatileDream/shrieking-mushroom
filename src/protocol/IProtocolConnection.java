package protocol;

import java.net.InetAddress;

import networking.exceptions.ConnectionClosedException;

public interface IProtocolConnection<M extends IMessage> {

	public InetAddress getAddress();

	public int getPort();

	public long lastReceived();
	
	public long lastSent();

	public boolean write( M m ) throws ConnectionClosedException;
	
}
