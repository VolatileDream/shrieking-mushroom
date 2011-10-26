package networking;

import java.net.InetAddress;

import networking.events.INetworkEvent;
import core.events.IEventQueue;
import core.threading.IRunner;

public class UDPConnectionBuilder {

	private IEventQueue<INetworkEvent> queue;
	private int port;
	private final UDPNetworkAccess access;

	public UDPConnectionBuilder(UDPNetworkAccess mna) {
		access = mna;
	}

	public void withPort(int p) {
		port = p;
	}

	public void withQueue(IEventQueue<INetworkEvent> q) {
		queue = q;
	}

	public IRunner subscribe(InetAddress net) {
		return access.subscribe(net, port, queue);
	}

}
