package protocol.implementation;

import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetReadEvent;
import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.INetworkEventsHandler;
import protocol.events.IProtocolEvent;
import core.events.IEventQueue;
import core.threading.IResetableStopper;
import core.threading.IRunner;
import core.threading.implementation.Stopper;

class MessageMover<M extends IMessage> implements Runnable, IRunner {

	private final Object threadLock = new Object();
	private final INetworkEventsHandler<M> handler;
	private final IEventQueue<INetworkEvent> networkQueue;
	private final IEventQueue<IProtocolEvent<M>> protocolQueue;

	private final IResetableStopper stopper;

	private Thread thread;

	public MessageMover( INetworkEventsHandler<M> h, IEventQueue<INetworkEvent> e, IEventQueue<IProtocolEvent<M>> e2 ){
		handler = h;
		networkQueue = e;
		protocolQueue = e2;
		stopper = new Stopper();
	}

	@Override
	public void run(){
		while( !stopper.hasStopped() ){

			if( !networkQueue.poll() ){
				try {
					Thread.sleep(1000);
				} catch ( InterruptedException e) {}
				continue;
			}
			INetworkEvent ev = networkQueue.remove();

			IProtocolEvent<M> returnEvent = null;
			
			if( ev instanceof INetConnectEvent ){

				returnEvent = handler.handleConnect( (INetConnectEvent)ev );

			} else if( ev instanceof INetCloseEvent ){

				returnEvent = handler.handleClose( (INetCloseEvent) ev );

			} else if( ev instanceof INetErrorEvent ){

				returnEvent = handler.handleError( (INetErrorEvent) ev );

			} else if( ev instanceof INetReadEvent ){

				returnEvent = handler.handleRead( (INetReadEvent) ev );

			} else {

				returnEvent = handler.handleUnknown( ev );
			}

			if( returnEvent != null ){
				protocolQueue.offer( returnEvent );
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
