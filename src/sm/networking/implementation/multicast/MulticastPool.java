package sm.networking.implementation.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import shriekingmushroom.threading.IResetableStopper;
import shriekingmushroom.threading.IRunner;
import shriekingmushroom.threading.IStopableStopper;
import shriekingmushroom.threading.IStopper;
import shriekingmushroom.threading.IWaiter;
import shriekingmushroom.threading.implementation.DisjointStopper;
import shriekingmushroom.threading.implementation.Stopper;
import sm.CommonAccessObject;
import sm.logging.ILogger.LogLevel;
import sm.networking.MulticastNetworkAccess;
import sm.networking.events.INetworkEvent;
import sm.networking.implementation.ConnectionFactory;
import sm.networking.implementation.interfaces.InternalConnection;

public class MulticastPool implements MulticastNetworkAccess, Runnable {

	private final CommonAccessObject cao;
	
	private final IWaiter wait;
	private final IResetableStopper stopper = new Stopper();
	
	private final ConnectionFactory fac;
	
	private final ExecutorService pool = Executors.newCachedThreadPool();
	private final List<InternalConnection> connections = new ArrayList<InternalConnection>();
	
	public MulticastPool( CommonAccessObject c, IWaiter w ){
		cao = c;
		wait = w;
		fac = new ConnectionFactory(c);
		pool.execute( this );
	}
	
	void started( InternalConnection multicastConnection ){
		synchronized (connections) {
			connections.add( multicastConnection );
		}
	}
	
	@Override
	public void run(){
		while( !stopper.hasStopped() ){
			
			List<InternalConnection> removals = new ArrayList<InternalConnection>();
			
			for( InternalConnection c : connections ){
				
				if( c.isClosed() ){
					removals.add( c );
					continue;
				}
				
				if( c.numSend() > 0 ){
					try {
						c.flush();
					} catch (IOException e) {
						cao.log.Log(e, LogLevel.Warn);
					}
				}
				
			}
			
		}//end !stop
	}
	
	@Override
	public void close() {
		stopper.setStop();
		pool.shutdown();
		pool.shutdownNow();
	}

	@Override
	public IRunner subscribe( NetworkInterface nif, InetAddress net, int port, BlockingQueue<INetworkEvent> queue) {
		IStopableStopper st = new Stopper();
		IStopper stopIt = new DisjointStopper( stopper, st );
		
		InternalConnection multiCon = null;
		try {
			multiCon = fac.ConstructMulticast(nif, net, port);
		} catch (SocketException e1) {
			cao.log.Log(e1, LogLevel.Error);
		} catch (IOException e2) {
			cao.log.Log(e2, LogLevel.Warn);
		}
		
		if( multiCon == null ){
			return null;
		}
		
		MulticastReciever mr = new MulticastReciever( multiCon, queue, wait, stopIt, cao, this );
		
		MulticastRunner run = new MulticastRunner( mr, st, pool );
		
		return run;
	}

}
