package orb.quantum.shriek.multicast;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import orb.quantum.shriek.tcp.TcpConnection;

public class MulticastConnection implements TcpConnection {

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean write(ByteBuffer buf) throws ClosedChannelException {
		// TODO Auto-generated method stub
		return false;
	}

}
