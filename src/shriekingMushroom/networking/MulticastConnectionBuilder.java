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

	public MulticastConnectionBuilder withPort(int p) {
		port = p;
		return this;
	}

	public MulticastConnectionBuilder withQueue(BlockingQueue<INetworkEvent> q) {
		queue = q;
		return this;
	}

	public IRunner subscribe(InetAddress inet) {
		return access.subscribe(inet, port, queue);
	}

}
