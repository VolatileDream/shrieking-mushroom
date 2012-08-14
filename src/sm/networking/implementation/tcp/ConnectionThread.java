package sm.networking.implementation.tcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import shriekingmushroom.threading.IResetableStopper;
import shriekingmushroom.threading.IStopper;
import shriekingmushroom.threading.IWaiter;
import shriekingmushroom.threading.implementation.DisjointStopper;
import shriekingmushroom.threading.implementation.Stopper;
import sm.CommonAccessObject;
import sm.logging.ILogger.LogLevel;
import sm.networking.IConnection;
import sm.networking.events.INetErrorEvent;
import sm.networking.events.INetReadEvent;
import sm.networking.events.INetworkEvent;
import sm.networking.implementation.events.ErrorEvent;
import sm.networking.implementation.events.ReadEvent;
import sm.networking.implementation.interfaces.InternalConnection;


public class ConnectionThread implements Runnable {

	private final CommonAccessObject cao;
	private final BlockingQueue<INetworkEvent> eventQueue;
	private final ArrayList<InternalConnection> connections = new ArrayList<InternalConnection>();

	private final IResetableStopper localStop = new Stopper();
	private final IStopper allStop;
	private final IWaiter wait;

	public ConnectionThread(CommonAccessObject c, IWaiter w,
			BlockingQueue<INetworkEvent> eq, IStopper s) {
		cao = c;
		eventQueue = eq;
		wait = w;
		allStop = new DisjointStopper(localStop, s);
	}

	@Override
	public void run() {

		while (!allStop.hasStopped()) {

			ArrayList<InternalConnection> removals = new ArrayList<InternalConnection>();

			// go through all the connections
			for (int i = 0; i < connections.size(); i++) {

				InternalConnection con = connections.get(i);

				if (con.isClosed()) {
					removals.add(con);
					continue;
				}

				try {

					handleConnection(con);

				} catch (IOException e) {

					removals.add(con);

					cao.log.Log(e, LogLevel.Error);

					INetErrorEvent er = new ErrorEvent(con);

					if (!eventQueue.offer(er)) {
						cao.log.Log("Couldn't add error event to queue", LogLevel.Error);
					}

					try {
						con.close();
					} catch (IOException e1) {
					}

				}// end error handling

			}// end loop through all connections

			synchronized (connections) {
				for (InternalConnection con : removals) {
					connections.remove(con);
				}
			}

			try {
				wait.doWait();
			} catch (InterruptedException e) {
				cao.log.Log(e, LogLevel.Warn);
			}

		}// end running loop

		// TODO cleanup connections

	}

	/**
	 * Handles the connection
	 * 
	 * @param con
	 *            the connection to handle
	 * @throws IOException
	 */
	private void handleConnection(InternalConnection con) throws IOException {

		if (con.numSend() > 0) {
			con.flush();
		}// end send check

		byte[] read = con.read();
		if (read != null && read.length > 0) {
			INetReadEvent evt = new ReadEvent(con, read);
			if (!eventQueue.offer(evt)) {
				cao.log.Log("Couldn't add read event to the queue",LogLevel.Error);
			}
		}
	}

	public void close() {
		localStop.setStop();
	}

	public boolean addConnection(InternalConnection c) {
		synchronized (connections) {
			if (connections.size() >= maxConnectionPerThread()) {
				return false;
			} else {
				connections.add(c);
				return true;
			}
		}
	}

	public int numConnections() {
		return connections.size();
	}

	public ArrayList<IConnection> getConnections() {
		ArrayList<IConnection> result = new ArrayList<IConnection>();
		result.addAll(connections);
		return result;
	}

	private int maxConnectionPerThread() {
		String var = "networking.unicast.max_thread_connections";
		return cao.handler.GetRequiredVariableAsInt(var, cao.store);
	}

}
