package shriekingMushroom.protocol.implementation;

import java.util.concurrent.BlockingQueue;

import shriekingMushroom.networking.events.INetCloseEvent;
import shriekingMushroom.networking.events.INetConnectEvent;
import shriekingMushroom.networking.events.INetErrorEvent;
import shriekingMushroom.networking.events.INetReadEvent;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.INetworkEventsHandler;
import shriekingMushroom.protocol.events.IProtocolEvent;
import shriekingMushroom.threading.IResetableStopper;
import shriekingMushroom.threading.IRunner;
import shriekingMushroom.threading.IWaiter;
import shriekingMushroom.threading.implementation.Stopper;

class MessageMover<M extends IMessage> implements Runnable, IRunner {

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
