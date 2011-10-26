package networking;

import java.net.InetAddress;

import networking.events.INetworkEvent;
import core.events.IEventQueue;
import core.threading.IRunner;

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
