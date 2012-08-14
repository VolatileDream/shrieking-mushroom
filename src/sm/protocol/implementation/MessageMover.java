package sm.protocol.implementation;

import java.util.concurrent.BlockingQueue;

import shriekingmushroom.threading.IResetableStopper;
import shriekingmushroom.threading.IRunner;
import shriekingmushroom.threading.IWaiter;
import shriekingmushroom.threading.implementation.Stopper;
import sm.networking.events.INetCloseEvent;
import sm.networking.events.INetConnectEvent;
import sm.networking.events.INetErrorEvent;
import sm.networking.events.INetReadEvent;
import sm.networking.events.INetworkEvent;
import sm.protocol.IMessage;
import sm.protocol.INetworkEventsHandler;
import sm.protocol.events.IProtocolEvent;

public class MessageMover<M extends IMessage> implements Runnable, IRunner {

	private final Object threadLock = new Object();
	private final INetworkEventsHandler<M> handler;
	private final BlockingQueue<INetworkEvent> networkQueue;
	private final BlockingQueue<IProtocolEvent<M>> protocolQueue;

	private final IWaiter waiter;
	private final IResetableStopper stopper;

	private Thread thread;

	public MessageMover(IWaiter wait, INetworkEventsHandler<M> h,
			BlockingQueue<INetworkEvent> e, BlockingQueue<IProtocolEvent<M>> e2) {
		if (wait == null || h == null || e == null || e2 == null) {
			throw new RuntimeException("An argument was null: (IWaiter " + wait
					+ ", INetworkEventsHandler " + h + ", IEventQueue " + e
					+ ", IEventQueue " + e2 + " )");
		}
		handler = h;
		networkQueue = e;
		protocolQueue = e2;
		stopper = new Stopper();
		waiter = wait;
	}

	@Override
	public void run() {
		while (!stopper.hasStopped()) {

			if (networkQueue.isEmpty()) {
				try {
					waiter.doWait();
				} catch (InterruptedException e) {
				}
				continue;
			}
			INetworkEvent ev = networkQueue.remove();

			IProtocolEvent<M> returnEvent = null;

			if (ev instanceof INetConnectEvent) {

				returnEvent = handler.handleConnect((INetConnectEvent) ev);

			} else if (ev instanceof INetCloseEvent) {

				returnEvent = handler.handleClose((INetCloseEvent) ev);

			} else if (ev instanceof INetErrorEvent) {

				returnEvent = handler.handleError((INetErrorEvent) ev);

			} else if (ev instanceof INetReadEvent) {

				returnEvent = handler.handleRead((INetReadEvent) ev);

			} else {

				returnEvent = handler.handleUnknown(ev);
			}

			if (returnEvent != null) {
				protocolQueue.offer(returnEvent);
			}

		}
	}

	@Override
	public void start() {
		synchronized (threadLock) {
			if (thread != null) {
				throw new RuntimeException(
						"Can't start a thread more then once.");
			}
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void stop() {
		synchronized (threadLock) {
			stopper.setStop();
		}
	}

}
