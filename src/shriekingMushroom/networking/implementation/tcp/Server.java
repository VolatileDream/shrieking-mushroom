package shriekingMushroom.networking.implementation.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import shriekingMushroom.core.CommonAccessObject;
import shriekingMushroom.core.events.IEventQueue;
import shriekingMushroom.core.logging.ILogger.LogLevel;
import shriekingMushroom.core.threading.IResetableStopper;
import shriekingMushroom.core.threading.IRunner;
import shriekingMushroom.core.threading.IStopper;
import shriekingMushroom.core.threading.implementation.DisjointStopper;
import shriekingMushroom.core.threading.implementation.Stopper;
import shriekingMushroom.networking.events.INetworkEvent;


public class Server implements Runnable, IRunner {

	private final CommonAccessObject cao;
	private final TCPConnectionPool pool;
	private final int port;
	private final IEventQueue<INetworkEvent> queue;

	private final Object threadLock = new Object();
	private Thread thread = null;

	private final IResetableStopper localStop = new Stopper();
	private final IStopper allStop;

	public Server(TCPConnectionPool p, IStopper s, CommonAccessObject c,
			int port, IEventQueue<INetworkEvent> q) {
		cao = c;
		pool = p;
		this.port = port;
		queue = q;
		allStop = new DisjointStopper(localStop, s);
	}

	public void start() {

		synchronized (threadLock) {
			if (thread != null) {
				cao.log
						.Log("Calling Server.Run more then once.",
								LogLevel.Warn);
				return;
			}
			thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {

		ServerSocket soc = null;
		try {
			soc = new ServerSocket(port);
			soc.setSoTimeout(getWaitTimeOut());
		} catch (IOException e2) {
			cao.log.Log(e2, LogLevel.Fatal);
			return;
		}

		while (!allStop.hasStopped()) {

			try {
				TCPConnection connect = null;

				soc.setSoTimeout(getWaitTimeOut());

				Socket connectionSocket = soc.accept();
				connectionSocket.setKeepAlive(true);

				connect = new TCPConnection(cao, connectionSocket);

				pool.addConnection(connect, queue);

			} catch (SocketTimeoutException e1) {
				// ignore it, we don't mind if it times out.
			} catch (IOException e1) {
				cao.log.Log(e1, LogLevel.Error);
			}

		}// end loop

		if (soc != null) {
			try {
				soc.close();
			} catch (IOException e) {
				cao.log.Log(e, LogLevel.Info);
			}
		}

	}

	public void stop() {
		localStop.setStop();
	}

	public void restart() {
		synchronized (threadLock) {
			if (thread != null) {
				stop();
				while (thread.isAlive()) {
					try {
						thread.join();
					} catch (InterruptedException e) {
					}
				}
				thread = null;
			}
		}
		localStop.reset();
		start();
	}

	private int getWaitTimeOut() {
		String timeOutVar = "networking.unicast.server_wait_timeout";
		return cao.handler.GetRequiredVariableAsInt(timeOutVar, cao.store);
	}

}
