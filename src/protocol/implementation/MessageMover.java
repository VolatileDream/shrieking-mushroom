package protocol.implementation;

import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetworkEvent;
import networking.events.INetReadEvent;
import protocol.INetworkEventsHandler;
import core.events.IEventQueue;

public class MessageMover implements Runnable {
	
	private final Object threadLock = new Object();
	private final INetworkEventsHandler handler;
	private final IEventQueue<INetworkEvent> queue;
	
	private Boolean keepRunning = false;
	private Thread thread;
	
	
	public MessageMover( INetworkEventsHandler h, IEventQueue<INetworkEvent> e ){
		handler = h;
		queue = e;
	}
	
	@Override
	public void run(){
		while( keepRunning ){
			
			queue.waitFor();
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

	public void start(){
		synchronized( threadLock ){
			if( thread != null ){
				throw new RuntimeException("Can't start a thread more then once.");
			}
			thread = new Thread( this );
			thread.start();
		}
	}
	
	public void stop(){
		synchronized( threadLock ){
			keepRunning = false;
		}
	}
	
}
