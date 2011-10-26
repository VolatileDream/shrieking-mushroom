package networking.implementation;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

import networking.exceptions.ConnectionClosedException;
import networking.implementation.interfaces.InternalConnection;
import core.CommonAccessObject;
import core.threading.ITimeMarker;
import core.threading.implementation.TimeMark;

public abstract class Connection implements InternalConnection {
	
	private final Queue<byte[]> sendQueue;
	private final InetAddress addr;
	private final int port;
	protected final CommonAccessObject cao;
	
	protected ITimeMarker lastReceive = new TimeMark();
	protected ITimeMarker lastSent = new TimeMark();

	protected ConnectionStatus status = ConnectionStatus.Closed; 

	protected Connection( CommonAccessObject c, InetAddress address, int port ){
		sendQueue = new LinkedList<byte[]>();
		this.addr = address;
		this.port = port;
		cao = c;
	}
	
	@Override
	public InetAddress getAddress(){
		return addr;
	}
	
	@Override
	public int getPort(){
		return port;
	}
	
	@Override
	public long lastSent(){
		return lastSent.getMark();
	}
	
	@Override
	public long lastReceived(){
		return lastReceive.getMark();
	}

	@Override
	public boolean isClosed(){
		return status == ConnectionStatus.Closed;
	}
	
	protected byte[][] getAndClearMessages() {
		byte[][] tmp = null;
		
		synchronized( sendQueue ){
			
			int queueSize = sendQueue.size();
			
			if( queueSize > 0 ){
				tmp = new byte[queueSize][];
			}
			
			int i=0;
			while( !sendQueue.isEmpty() ){
				tmp[i] = sendQueue.remove();
				i++;
			}
		}
		
		return tmp;
	}

	@Override
	public int numSend() {
		int num = 0;
		
		synchronized( sendQueue ){
			num = sendQueue.size();
		}
		
		return num;
	}

	@Override
	public boolean write( byte[] m ) throws ConnectionClosedException {

		if( status == ConnectionStatus.Closed ){
			throw new ConnectionClosedException("Connection has been closed");
		}
		boolean added = false;

		synchronized( sendQueue ){
			
			added = sendQueue.offer( m );
			
		}
		return added;
	}

}
