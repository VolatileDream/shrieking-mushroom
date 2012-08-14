package sm.protocol.implementation;

import java.util.concurrent.BlockingQueue;

import shriekingmushroom.events.QueueBuilder;
import shriekingmushroom.threading.IRunner;
import shriekingmushroom.threading.IWaiter;
import shriekingmushroom.threading.implementation.CommonWaitTime;
import sm.CommonAccessObject;
import sm.networking.events.INetworkEvent;
import sm.protocol.IMessage;
import sm.protocol.INetworkEventsHandler;
import sm.protocol.events.IProtocolEvent;
import sm.util.Tupple;

public class ProtocolSetup<M extends IMessage> {

	private final INetworkEventsHandler<M> handle;
	private final CommonAccessObject cao;
	private final QueueBuilder builder = new QueueBuilder();

	public ProtocolSetup(CommonAccessObject c, INetworkEventsHandler<M> h) {
		handle = h;
		cao = c;
	}

	public Tupple<IRunner, BlockingQueue<IProtocolEvent<M>>> build( BlockingQueue<INetworkEvent> queue) {
		
		BlockingQueue<IProtocolEvent<M>> q = builder.buildQueue();
		IRunner run = this.build(queue, q);
		return new Tupple<IRunner, BlockingQueue<IProtocolEvent<M>>>(run, q);
	}

	public IRunner build(BlockingQueue<INetworkEvent> queue, BlockingQueue<IProtocolEvent<M>> outEvents) {

		int queueWaitTime = cao.handler.GetRequiredVariableAsInt(
				"queue.poll_time_milli", cao.store);

		IWaiter networkWait = new CommonWaitTime(queueWaitTime);
		MessageMover<M> mover = new MessageMover<M>(networkWait, handle, queue, outEvents);

		return mover;
	}

}
