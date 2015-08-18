package orb.quantum.shriek.common;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import orb.quantum.shriek.threading.IoHandler;

public interface Connection extends AutoCloseable {

	public IoHandler getHandler();
	
	public boolean write( ByteBuffer buf ) throws ClosedChannelException ;

	public void close() throws Exception ;

}
