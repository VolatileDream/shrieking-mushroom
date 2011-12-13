package shriekingMushroom.networking.implementation.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import shriekingMushroom.CommonAccessObject;
import shriekingMushroom.logging.ILogger.LogLevel;
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
import shriekingMushroom.threading.IResetableStopper;
import shriekingMushroom.threading.IRestartable;
import shriekingMushroom.threading.IWaiter;
import shriekingMushroom.threading.implementation.Stopper;


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

	void addConnection(final InternalConnection con, BlockingQueue<INetworkEvent> queue) {

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
			cao.log.Log("Forced to close a connection", LogLevel.Error);
			try {
				con.close();
			} catch (IOException e) {}
		}
	}

	private ConnectionThread newThread(ArrayList<ConnectionThread> threads, BlockingQueue<INetworkEvent> e) {
		ConnectionThread ct = new ConnectionThread(cao, wait, e, stop);

		Thread t = new Thread(ct);
		t.start();

		threads.add(ct);

		return ct;
	}

	// interfaces

	@Override
	public void connect(InetAddress net, int port, BlockingQueue<INetworkEvent> queue) {
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
	public IRestartable allowConnection(int port, BlockingQueue<INetworkEvent> q) {
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
