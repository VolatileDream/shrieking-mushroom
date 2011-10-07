package protocol.implementation;

import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetReadEvent;
import networking.events.INetworkEvent;
import protocol.INetworkEventsHandler;
import protocol.IProtocolBlock;
import core.events.IEventQueue;
import core.threading.IResetableStopper;
import core.threading.implementation.Stopper;

public class MessageMover implements Runnable, IProtocolBlock {
	
	private final Object threadLock = new Object();
	private final INetworkEventsHandler handler;
	private final IEventQueue<INetworkEvent> queue;
	
	private final IResetableStopper stopper;
	
	private Thread thread;
	
	public MessageMover( INetworkEventsHandler h, IEventQueue<INetworkEvent> e ){
		handler = h;
		queue = e;
		stopper = new Stopper();
	}
	
	@Override
	public void run(){
		while( !stopper.hasStopped() ){
			
			queue.waitFor( stopper );
			INetworkEvent ev = queue.remove();
			
			if( ev instanceof INetConnectEvent ){
				handler.handleConnect( (INetConnectEvent)ev );
			}else if( ev instanceof INetCloseEvent ){
				handler.handleClose( (INetCloseEvent) ev );
			}else if( ev instanceof INetErrorEvent ){
				handler.handleError( (INetErrorEvent) ev );
			}else if( ev instanceof INetReadEvent ){
				handler.handleRead( (INetReadEvent) ev );
			}else {
				handler.handleUnknown( ev );
			}
			
		}
	}

	@Override
	public void start(){
		synchronized( threadLock ){
			if( thread != null ){
				throw new RuntimeException("Can't start a thread more then once.");
			}
			thread = new Thread( this );
			thread.start();
		}
	}
	
	@Override
	public void stop(){
		synchronized( threadLock ){
			stopper.setStop();
		}
	}
	
}
