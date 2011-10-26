package shriekingMushroom.networking;

import java.net.InetAddress;

import shriekingMushroom.core.events.IEventQueue;
import shriekingMushroom.core.threading.IRunner;
import shriekingMushroom.networking.events.INetworkEvent;


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
