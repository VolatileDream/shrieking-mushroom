package shriekingMushroom.networking;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.threading.IRunner;


public class TCPConnectionBuilder {

	private BlockingQueue<INetworkEvent> queue = null;
	private int port;
	private final TCPNetworkAccess access;

	public TCPConnectionBuilder(TCPNetworkAccess a) {
		access = a;
	}

	public TCPNetworkAccess unwrap(){
		return access;
	}
	
	public TCPConnectionBuilder withPort(int p) {
		port = p;
		return this;
	}

	public TCPConnectionBuilder withQueue(BlockingQueue<INetworkEvent> q) {
		queue = q;
		return this;
	}

	public void connect(InetAddress inet) {
		access.connect(inet, port, queue);
	}

	public IRunner allowConnection() {
		return access.allowConnection(port, queue);
	}

}
