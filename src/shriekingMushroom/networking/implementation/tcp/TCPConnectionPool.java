package shriekingMushroom.networking.implementation.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import shriekingMushroom.core.CommonAccessObject;
import shriekingMushroom.core.events.IEventQueue;
import shriekingMushroom.core.logging.ILogger.LogLevel;
import shriekingMushroom.core.threading.IResetableStopper;
import shriekingMushroom.core.threading.IRunner;
import shriekingMushroom.core.threading.IWaiter;
import shriekingMushroom.core.threading.implementation.Stopper;
import shriekingMushroom.networking.IConnection;
import shriekingMushroom.networking.TCPNetworkAccess;
import shriekingMushroom.networking.events.INetConnectEvent;
import shriekingMushroom.networking.events.INetErrorEvent;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.networking.implementation.ConnectionFactory;
import shriekingMushroom.networking.implementation.NullConnection;
import shriekingMushroom.networking.implementation.events.ConnectEvent;
import shriekingMushroom.networking.implementation.events.ErrorEvent;
import shriekingMushroom.networking.implementation.interfaces.InternalConnection;


public class TCPConnectionPool implements TCPNetworkAccess {

	private final CommonAccessObject cao;
	private final ConnectionFactory fac;

	private final ArrayList<ConnectionThread> threads = new ArrayList<ConnectionThread>();

	private final IResetableStopper stop = new Stopper();
	private final IWaiter wait;

	public TCPConnectionPool(CommonAccessObject c, IWaiter w) {
		cao = c;
		fac = new ConnectionFactory(cao);
		wait = w;
	}

	void addConnection(final InternalConnection con,
			IEventQueue<INetworkEvent> queue) {

		boolean added = false;

		for (ConnectionThread ct : threads) {

			added = ct.addConnection(con);
			if (added) {
				break;
			}
		}

		if (!added && !stop.hasStopped()) {

			ConnectionThread ct = newThread(threads, queue);

			added = ct.addConnection(con);

			if (!added) {
				cao.log.Log("Unable to add the connection to a new thread",
						LogLevel.Fatal);
			}
		}
		if (added) {
			INetConnectEvent event = new ConnectEvent(con);
			queue.offer(event);
		} else {
			try {
				cao.log.Log("Forced to close a connection", LogLevel.Error);
				con.close();
			} catch (IOException e) {
			}
		}
	}

	private ConnectionThread newThread(ArrayList<ConnectionThread> threads,
			IEventQueue<INetworkEvent> e) {
		ConnectionThread ct = new ConnectionThread(cao, wait, e, stop);

		Thread t = new Thread(ct);
		t.start();

		threads.add(ct);

		return ct;
	}

	// interfaces

	@Override
	public void connect(InetAddress net, int port,
			IEventQueue<INetworkEvent> queue) {
		Client c = new Client(fac, cao, net, port);

		try {

			InternalConnection con = c.Connect();
			this.addConnection(con, queue);

		} catch (IOException e) {
			cao.log.Log(e, LogLevel.Error);
			IConnection con = new NullConnection(net, port);
			INetErrorEvent event = new ErrorEvent(con);
			queue.offer(event);
		}
	}

	@Override
	public IRunner allowConnection(int port, IEventQueue<INetworkEvent> q) {
		return new Server(this, stop, cao, port, q);
	}

	@Override
	public ArrayList<IConnection> getConnections() {
		ArrayList<IConnection> connections = new ArrayList<IConnection>();
		for (ConnectionThread ct : threads) {
			connections.addAll(ct.getConnections());
		}
		return connections;
	}

	@Override
	public void close() {
		stop.setStop();
	}
}
