package sm.networking;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

import shriekingmushroom.threading.IRunner;
import sm.networking.events.INetworkEvent;


public class UDPConnectionBuilder {

	private BlockingQueue<INetworkEvent> queue;
	private int port;
	private final UDPNetworkAccess access;

	public UDPConnectionBuilder(UDPNetworkAccess mna) {
		access = mna;
	}

	public UDPNetworkAccess unwrap(){
		return access;
	}
	
	public UDPConnectionBuilder withPort(int p) {
		port = p;
		return this;
	}

	public UDPConnectionBuilder withQueue(BlockingQueue<INetworkEvent> q) {
		queue = q;
		return this;
	}

	public IRunner subscribe(InetAddress net) {
		return access.subscribe(net, port, queue);
	}

}
