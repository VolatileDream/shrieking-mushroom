package shriekingMushroom.protocol.implementation;

import java.util.concurrent.BlockingQueue;

import shriekingMushroom.CommonAccessObject;
import shriekingMushroom.events.QueueBuilder;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.INetworkEventsHandler;
import shriekingMushroom.protocol.events.IProtocolEvent;
import shriekingMushroom.threading.IRunner;
import shriekingMushroom.threading.IWaiter;
import shriekingMushroom.threading.implementation.CommonWaitTime;
import shriekingMushroom.util.Tupple;

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
