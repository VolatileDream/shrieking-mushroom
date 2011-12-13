package shriekingMushroom.networking;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.BlockingQueue;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.threading.IRunner;

public class MulticastConnectionBuilder {

	private final MulticastNetworkAccess access;
	private int port;
	private NetworkInterface nif;
	private BlockingQueue<INetworkEvent> queue;

	public MulticastConnectionBuilder(MulticastNetworkAccess a) {
		access = a;
	}

	public MulticastNetworkAccess unwrap(){
		return access;
	}
	
	public MulticastConnectionBuilder withPort(int p) {
		port = p;
		return this;
	}

	public MulticastConnectionBuilder withQueue(BlockingQueue<INetworkEvent> q) {
		queue = q;
		return this;
	}
	
	public MulticastConnectionBuilder withNetworkInterface( NetworkInterface n ){
		nif = n;
		return this;
	}

	public IRunner subscribe(InetAddress inet) {
		return access.subscribe(nif, inet, port, queue);
	}

}
