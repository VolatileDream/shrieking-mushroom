package shriekingMushroom.networking;

import java.net.InetAddress;

import shriekingMushroom.core.events.IEventQueue;
import shriekingMushroom.core.threading.IRunner;
import shriekingMushroom.networking.events.INetworkEvent;


public class TCPConnectionBuilder {

	private IEventQueue<INetworkEvent> queue = null;
	private int port;
	private final TCPNetworkAccess access;

	public TCPConnectionBuilder(TCPNetworkAccess a) {
		access = a;
	}

	public void withPort(int p) {
		port = p;
	}

	public void withQueue(IEventQueue<INetworkEvent> q) {
		queue = q;
	}

	public void connect(InetAddress inet) {
		access.connect(inet, port, queue);
	}

	public IRunner allowConnection() {
		return access.allowConnection(port, queue);
	}

}
