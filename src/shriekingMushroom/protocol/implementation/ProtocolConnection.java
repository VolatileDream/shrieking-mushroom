package shriekingMushroom.protocol.implementation;

import java.io.IOException;
import java.net.InetAddress;

import shriekingMushroom.CommonAccessObject;
import shriekingMushroom.logging.ILogger.LogLevel;
import shriekingMushroom.networking.IConnection;
import shriekingMushroom.networking.exceptions.ConnectionClosedException;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.IMessageFactory;
import shriekingMushroom.protocol.IProtocolConnection;

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
	
}
