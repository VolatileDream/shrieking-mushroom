package shriekingMushroom.networking;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.threading.IRunner;

public class MulticastConnectionBuilder {

	private final MulticastNetworkAccess access;
	private int port;
	private BlockingQueue<INetworkEvent> queue;

	public MulticastConnectionBuilder(MulticastNetworkAccess a) {
		access = a;
	}

	public void withPort(int p) {
		port = p;
	}

	public void withQueue(BlockingQueue<INetworkEvent> q) {
		queue = q;
	}

	public IRunner subscribe(InetAddress inet) {
		return access.subscribe(inet, port, queue);
	}

}
