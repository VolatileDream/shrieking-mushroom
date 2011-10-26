package networking;

import java.net.InetAddress;

import networking.events.INetworkEvent;
import core.events.IEventQueue;
import core.threading.IRunner;

public class MulticastConnectionBuilder {

	private final MulticastNetworkAccess access;
	private int port;
	private IEventQueue<INetworkEvent> queue;

	public MulticastConnectionBuilder(MulticastNetworkAccess a) {
		access = a;
	}

	public void withPort(int p) {
		port = p;
	}

	public void withQueue(IEventQueue<INetworkEvent> q) {
		queue = q;
	}

	public IRunner subscribe(InetAddress inet) {
		return access.subscribe(inet, port, queue);
	}

}
