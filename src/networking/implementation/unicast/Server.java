package networking.implementation.unicast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import networking.TCPServer;
import core.CommonAccessObject;
import core.CommonVarFetch;
import core.logging.ILogger.LogLevel;
import core.threading.IResetableStopper;
import core.threading.IStopper;
import core.threading.implementation.DisjointStopper;
import core.threading.implementation.Stopper;

public class Server implements Runnable, TCPServer {

	private final CommonAccessObject cao;
	private final TCPConnectionPool pool;
	private final int port;

	private final Object threadLock = new Object();
	private Thread thread = null;

	private final IResetableStopper localStop = new Stopper();
	private final IStopper allStop;

	public Server( TCPConnectionPool p, IStopper s, CommonAccessObject c, int port ){
		cao = c;
		pool = p;
		this.port = port;
		allStop = new DisjointStopper( localStop, s );
	}

	public void start(){

		synchronized( threadLock ){
			if( thread != null ){
				cao.log.Log("Calling Server.Run more then once.", LogLevel.Warn );
				return;
			}
			thread = new Thread( this );
			thread.start();
		}
	}

	public void run(){

		CommonVarFetch fetcher = new CommonVarFetch( cao.store, cao.handler );
		
		ServerSocket soc = null;
		try {
			soc = new ServerSocket( port );
			soc.setSoTimeout( getWaitTimeOut() );
		} catch (IOException e2) {
			cao.log.Log(e2, LogLevel.Fatal);
			return;
		}

		while( ! allStop.hasStopped() ){

			try {
				UnicastConnection connect = null;

				soc.setSoTimeout( getWaitTimeOut() );

				Socket connectionSocket = soc.accept();
				connectionSocket.setKeepAlive( true );

				connect = new UnicastConnection( cao, connectionSocket );

				pool.addServerConnection( connect );

			} catch( SocketTimeoutException e1) {
				//ignore it, we don't mind if it times out.
			} catch (IOException e1) {
				cao.log.Log(e1, LogLevel.Error );
			}

			try {
				long sleep = fetcher.threadingSleep();
				Thread.sleep( sleep );
			} catch (InterruptedException e) {}

		}// end loop

		if( soc != null ){
			try {
				soc.close();
			} catch (IOException e) {
				cao.log.Log(e, LogLevel.Info);
			}
		}
		
	}

	public void stop(){
		localStop.setStop();
	}

	public void restart(){
		synchronized( threadLock ){
			if( thread != null ){
				stop();
				while( thread.isAlive() ){
					try {
						thread.join();
					} catch (InterruptedException e) {}
				}
				thread = null;
			}
		}
		localStop.reset();
		start();
	}

	private int getWaitTimeOut(){
		String timeOutVar = "networking.unicast.server_wait_timeout";
		return cao.handler.GetRequiredVariableAsInt( timeOutVar, cao.store );
	}

}
