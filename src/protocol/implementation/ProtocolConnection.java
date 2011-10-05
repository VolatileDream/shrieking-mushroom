package protocol.implementation;

import java.net.InetAddress;

import networking.IConnection;
import networking.exceptions.ConnectionClosedException;
import protocol.IMessage;
import protocol.IMessageFactory;
import protocol.IProtocolConnection;

public class ProtocolConnection<M extends IMessage> implements IProtocolConnection<M> {

	private final IConnection con;
	private final IMessageFactory<M> factory;
	
	public ProtocolConnection( IConnection c, IMessageFactory<M> f ){
		con = c;
		factory = f;
	}
	
	@Override
	public InetAddress getAddress() {
		return con.getAddress();
	}

	@Override
	public int getPort() {
		return con.getPort();
	}

	@Override
	public long lastReceived() {
		return con.lastReceived();
	}

	@Override
	public long lastSent() {
		return con.lastSent();
	}

	@Override
	public boolean write( M m ) throws ConnectionClosedException {
		byte[] write =  factory.transformToBytes( m );
		synchronized( con ){
			return con.write( write );
		}
	}

}
