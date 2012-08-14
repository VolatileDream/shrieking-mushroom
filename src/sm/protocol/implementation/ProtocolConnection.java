package sm.protocol.implementation;

import java.io.IOException;
import java.net.InetAddress;

import sm.CommonAccessObject;
import sm.logging.ILogger.LogLevel;
import sm.networking.IConnection;
import sm.networking.exceptions.ConnectionClosedException;
import sm.protocol.IMessage;
import sm.protocol.IMessageFactory;
import sm.protocol.IProtocolConnection;

public class ProtocolConnection<M extends IMessage> implements
		IProtocolConnection<M> {

	private final CommonAccessObject cao;
	private final IConnection con;
	private final IMessageFactory<M> factory;

	public ProtocolConnection(CommonAccessObject cao, IConnection con, IMessageFactory<M> f) {
		this.con = con;
		factory = f;
		this.cao = cao;
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
	public boolean write(M m) throws ConnectionClosedException {
		byte[] write = factory.transformToBytes(m);
		synchronized (con) {
			return con.write(write);
		}
	}

	@Override
	public void close(){
		try {
			con.close();
		} catch (IOException e) {
			cao.log.Log(e, LogLevel.Warn);
		}
	}
	
	@Override
	public boolean isClosed(){
		return con.isClosed();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals( Object o ){
		if( o instanceof ProtocolConnection ){
			ProtocolConnection c = (ProtocolConnection)o;
			return c.getPort() == getPort() && c.getAddress().equals( getAddress() );
		}
		return false;
	}
	
}
