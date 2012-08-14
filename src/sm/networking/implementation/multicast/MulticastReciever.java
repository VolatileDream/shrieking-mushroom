package sm.networking.implementation.multicast;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import shriekingmushroom.threading.IStopper;
import shriekingmushroom.threading.IWaiter;
import sm.CommonAccessObject;
import sm.logging.ILogger.LogLevel;
import sm.networking.events.INetConnectEvent;
import sm.networking.events.INetErrorEvent;
import sm.networking.events.INetReadEvent;
import sm.networking.events.INetworkEvent;
import sm.networking.implementation.events.ConnectEvent;
import sm.networking.implementation.events.ErrorEvent;
import sm.networking.implementation.events.ReadEvent;
import sm.networking.implementation.interfaces.InternalConnection;

public class MulticastReciever implements Runnable {
	
	private final CommonAccessObject cao;
	private final IWaiter wait;
	private final IStopper stopper;
	
	private final MulticastPool pool;
	
	private final InternalConnection multiCon;
	private final BlockingQueue<INetworkEvent> queue;
	
	public MulticastReciever( InternalConnection ic, BlockingQueue<INetworkEvent> e, IWaiter w, IStopper st, CommonAccessObject c, MulticastPool pool ){
		multiCon = ic;
		queue = e;
		wait = w;
		stopper = st;
		cao = c;
		this.pool = pool;
	}
	
	@Override
	public void run() {
		
		{
			//heh, this is a silly hack. but we kinda need to send the connection event
			INetConnectEvent evt = new ConnectEvent( multiCon );
			queue.offer( evt );
			pool.started( multiCon );
		}
		
		while( ! stopper.hasStopped() ){
			
			byte[] read = null;
			
			try {
				read = multiCon.read();
			} catch (IOException e) {
				cao.log.Log(e, LogLevel.Warn);
				INetErrorEvent evt = new ErrorEvent(multiCon);
				queue.offer( evt );
			}
			
			if( read != null && read.length > 0 ){
				INetReadEvent evt = new ReadEvent(multiCon, read);
				queue.offer( evt );
			}
			
			try {
				wait.doWait();
			} catch (InterruptedException e) {
				Thread.interrupted();//clear interrupt status
			}
			
		}//end stopper.hasStopped
		
		try {
			multiCon.close();
		} catch (IOException e) {
			cao.log.Log(e, LogLevel.Info);
		}
		
	}

}
