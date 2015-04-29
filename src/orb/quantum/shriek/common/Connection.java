package orb.quantum.shriek.common;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

public interface Connection extends AutoCloseable {

	public enum ConnectionType { TCP, UDP, MULTICAST };
	
	public ConnectionType getType();
	
	public boolean write( ByteBuffer buf ) throws ClosedChannelException ;

	public void close() throws Exception ;

}
