package networking.implementation;

import java.net.InetAddress;

import networking.IConnection;

public class NullConnection implements IConnection {

	private final InetAddress netAdr;
	private final int portNum;
	
	public NullConnection( InetAddress net, int port ){
		netAdr = net;
		portNum = port;
	}
	
	@Override
	public InetAddress getAddress() {
		return netAdr;
	}

	@Override
	public int getPort() {
		return portNum;
	}

	@Override
	public long lastReceived() {
		return 0;
	}

	@Override
	public long lastSent() {
		return 0;
	}

	@Override
	public boolean write(byte[] m) {
		return false;
	}

}
