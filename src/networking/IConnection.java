package networking;

import java.net.InetAddress;

public interface IConnection {

	public InetAddress getAddress();
	
	public int getPort();
	
	boolean write( byte[] m );
	
	/**
	 * Returns the last time the connection read something
	 * @return A long representation of the milliseconds since epoch, of the last successful read
	 */
	long lastReceived();
	
	/**
	 * Returns the last time the connection wrote something 
	 * @return A long representation of the milliseconds since epoch, of the last successful write
	 */
	long lastSent();
	
}
