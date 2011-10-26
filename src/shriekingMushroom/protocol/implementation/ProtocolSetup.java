package shriekingMushroom.protocol.implementation;

import shriekingMushroom.core.CommonAccessObject;
import shriekingMushroom.core.events.IEventQueue;
import shriekingMushroom.core.events.implementation.EventQueue;
import shriekingMushroom.core.threading.IRunner;
import shriekingMushroom.core.threading.IWaiter;
import shriekingMushroom.core.threading.implementation.CommonWaitTime;
import shriekingMushroom.core.util.Tupple;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.INetworkEventsHandler;
import shriekingMushroom.protocol.events.IProtocolEvent;

public class ProtocolSetup<M extends IMessage> {

	private final INetworkEventsHandler<M> handle;
	private final CommonAccessObject cao;

	public ProtocolSetup(CommonAccessObject c, INetworkEventsHandler<M> h) {
		handle = h;
		cao = c;
	}

	public Tupple<IRunner, IEventQueue<IProtocolEvent<M>>> build(
			IEventQueue<INetworkEvent> queue) {
		IEventQueue<IProtocolEvent<M>> q = new EventQueue<IProtocolEvent<M>>();
		IRunner run = this.build(queue, q);
		return new Tupple<IRunner, IEventQueue<IProtocolEvent<M>>>(run, q);
	}

	public IRunner build(IEventQueue<INetworkEvent> queue,
			IEventQueue<IProtocolEvent<M>> outEvents) {
		INetworkEventsHandler<M> eventHandle = new EventHandler<M>(handle);

		int queueWaitTime = cao.handler.GetRequiredVariableAsInt(
				"queue.poll_time_milli", cao.store);

		IWaiter networkWait = new CommonWaitTime(queueWaitTime);
		MessageMover<M> mover = new MessageMover<M>(networkWait, eventHandle,
				queue, outEvents);

		return mover;
	}

}
