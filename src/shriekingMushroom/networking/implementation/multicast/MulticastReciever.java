package shriekingMushroom.networking.implementation.multicast;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import shriekingMushroom.CommonAccessObject;
import shriekingMushroom.logging.ILogger.LogLevel;
import shriekingMushroom.networking.events.INetConnectEvent;
import shriekingMushroom.networking.events.INetErrorEvent;
import shriekingMushroom.networking.events.INetReadEvent;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.networking.implementation.events.ConnectEvent;
import shriekingMushroom.networking.implementation.events.ErrorEvent;
import shriekingMushroom.networking.implementation.events.ReadEvent;
import shriekingMushroom.networking.implementation.interfaces.InternalConnection;
import shriekingMushroom.threading.IStopper;
import shriekingMushroom.threading.IWaiter;

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
