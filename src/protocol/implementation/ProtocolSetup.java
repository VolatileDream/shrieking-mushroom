package protocol.implementation;

import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.INetworkEventsHandler;
import protocol.events.IProtocolEvent;
import core.CommonAccessObject;
import core.events.IEventQueue;
import core.events.implementation.EventQueue;
import core.threading.IRunner;
import core.threading.IWaiter;
import core.threading.implementation.CommonWaitTime;
import core.util.Tupple;

public class ProtocolSetup<M extends IMessage> {

	private final INetworkEventsHandler<M> handle;
	private final CommonAccessObject cao;

	public ProtocolSetup( CommonAccessObject c, INetworkEventsHandler<M> h ){
		handle = h;
		cao = c;
	}

	public Tupple<IRunner,IEventQueue<IProtocolEvent<M>>> build( IEventQueue<INetworkEvent> queue ){
		IEventQueue<IProtocolEvent<M>> q = new EventQueue<IProtocolEvent<M>>();
		IRunner run = this.build( queue, q );
		return new Tupple<IRunner,IEventQueue<IProtocolEvent<M>>>( run,q );
	}

	public IRunner build( IEventQueue<INetworkEvent> queue, IEventQueue<IProtocolEvent<M>> outEvents ){
		INetworkEventsHandler<M> eventHandle = new EventHandler<M>( handle );
		
		int queueWaitTime = cao.handler.GetRequiredVariableAsInt("queue.poll_time_milli", cao.store);
		
		IWaiter networkWait = new CommonWaitTime( queueWaitTime );
		MessageMover<M> mover = new MessageMover<M>( networkWait, eventHandle, queue, outEvents );

		return mover;
	}

}
