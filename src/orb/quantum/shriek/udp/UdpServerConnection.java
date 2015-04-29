package orb.quantum.shriek.udp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import orb.quantum.shriek.common.Connection;
import orb.quantum.shriek.events.EventBuilder;

class UdpServerConnection implements AutoCloseable {

	// These are the same set of SocketAddresses, if we don't have something in the connections
	// map, then we fail the blocking queue add and write. 
	private final Map<SocketAddress, UdpClientConnection> connections = new HashMap<>();
	private BlockingQueue<UdpWrite> writeQueue = new LinkedBlockingQueue<>();

	protected final EventBuilder builder;

	public UdpServerConnection( EventBuilder e ){
		builder = e;
	}

	private SelectionKey key;

	void attach( SelectionKey k ){
		key = k;
	}

	SelectionKey getKey(){
		return key;
	}

	UdpClientConnection connect( SocketAddress addr ) throws ClosedChannelException {
		synchronized(this){
			if( key == null ){
				throw new ClosedChannelException();
			}
		}
		synchronized(connections){
			UdpClientConnection c = connections.get(addr);
			if( c == null ){
				c = new UdpClientConnection(addr);
				connections.put(addr, c);
				builder.connectionCreated(c);
			}
			return c;
		}
	}

	@Override
	public void close() throws Exception {

		synchronized(this){

			key.cancel();
			key.channel().close();

			key = null;
		}

		synchronized(connections){
			for( UdpClientConnection client : connections.values() ){
				client.close();
			}
			connections.clear();
		}

		// We don't do this, because we don't have a "connection" with
		// a listening server socket.
		// builder.close(this);
	}

	boolean hasWrite(){
		return ! writeQueue.isEmpty();
	}

	UdpWrite fetchWrite(){

		UdpWrite write = writeQueue.poll();

		if( write != null ){
			// have to check that the write is still allowed
			// ie, that the client end hasn't been closed.
			synchronized(connections){
				if( !connections.containsKey(write.address) ){
					write = null;
				}
			}
		}

		return write;
	}

	class UdpClientConnection implements Connection {

		final SocketAddress addr;

		public UdpClientConnection(SocketAddress a){
			addr = a;
		}

		public boolean write( ByteBuffer buf ){
			
			if( buf == null ){
				throw new IllegalArgumentException("No buffer provided.");
			}
			
			synchronized (connections) {
				if(!connections.containsKey(this)){
					return false;
				}
			}
			return writeQueue.offer(new UdpWrite(buf, addr));
		}

		@Override
		public ConnectionType getType() {
			return ConnectionType.UDP;
		}

		@Override
		public void close() throws Exception {
			synchronized(connections){
				connections.remove(this);
				builder.connectionClose(this);
			}
		}
	}

	static class UdpWrite {
		final ByteBuffer buffer;
		final SocketAddress address;
		public UdpWrite( ByteBuffer buf, SocketAddress addr ){
			buffer = buf;
			address = addr;
		}
	}

}
