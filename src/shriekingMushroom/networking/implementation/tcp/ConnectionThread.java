package shriekingMushroom.networking.implementation.tcp;

import java.io.IOException;
import java.util.ArrayList;

import shriekingMushroom.core.CommonAccessObject;
import shriekingMushroom.core.events.IEventQueue;
import shriekingMushroom.core.logging.ILogger.LogLevel;
import shriekingMushroom.core.threading.IResetableStopper;
import shriekingMushroom.core.threading.IStopper;
import shriekingMushroom.core.threading.IWaiter;
import shriekingMushroom.core.threading.implementation.DisjointStopper;
import shriekingMushroom.core.threading.implementation.Stopper;
import shriekingMushroom.networking.IConnection;
import shriekingMushroom.networking.events.INetErrorEvent;
import shriekingMushroom.networking.events.INetReadEvent;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.networking.implementation.events.ErrorEvent;
import shriekingMushroom.networking.implementation.events.ReadEvent;
import shriekingMushroom.networking.implementation.interfaces.InternalConnection;


public class ConnectionThread implements Runnable {

	private final CommonAccessObject cao;
	private final IEventQueue<INetworkEvent> eventQueue;
	private final ArrayList<InternalConnection> connections = new ArrayList<InternalConnection>();

	private final IResetableStopper localStop = new Stopper();
	private final IStopper allStop;
	private final IWaiter wait;

	public ConnectionThread(CommonAccessObject c, IWaiter w,
			IEventQueue<INetworkEvent> eq, IStopper s) {
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
						cao.log.Log("Couldn't add error event to queue",
								LogLevel.Error);
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
				cao.log.Log("Couldn't add read event to the queue",
						LogLevel.Error);
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
