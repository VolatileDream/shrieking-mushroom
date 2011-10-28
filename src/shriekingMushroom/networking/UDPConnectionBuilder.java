package shriekingMushroom.networking;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.threading.IRunner;


public class UDPConnectionBuilder {

	private BlockingQueue<INetworkEvent> queue;
	private int port;
	private final UDPNetworkAccess access;

	public UDPConnectionBuilder(UDPNetworkAccess mna) {
		access = mna;
	}

	public void withPort(int p) {
		port = p;
	}

	public void withQueue(BlockingQueue<INetworkEvent> q) {
		queue = q;
	}

	public IRunner subscribe(InetAddress net) {
		return access.subscribe(net, port, queue);
	}

}
