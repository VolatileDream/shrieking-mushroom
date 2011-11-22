package shriekingMushroom.networking.implementation.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import shriekingMushroom.CommonAccessObject;
import shriekingMushroom.logging.ILogger.LogLevel;
import shriekingMushroom.networking.MulticastNetworkAccess;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.networking.implementation.ConnectionFactory;
import shriekingMushroom.networking.implementation.interfaces.InternalConnection;
import shriekingMushroom.threading.IResetableStopper;
import shriekingMushroom.threading.IRunner;
import shriekingMushroom.threading.IStopableStopper;
import shriekingMushroom.threading.IStopper;
import shriekingMushroom.threading.IWaiter;
import shriekingMushroom.threading.implementation.DisjointStopper;
import shriekingMushroom.threading.implementation.Stopper;

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
