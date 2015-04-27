package orb.quantum.shriek.udp;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import orb.quantum.shriek.common.Connection;

public class UdpConnection implements Connection {

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
