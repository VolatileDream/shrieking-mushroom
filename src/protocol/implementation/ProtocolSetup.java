package protocol.implementation;

import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.INetworkEventsHandler;
import protocol.events.IProtocolEvent;
import core.Tupple;
import core.events.IEventQueue;
import core.events.implementation.EventQueue;
import core.threading.IRunner;

public class ProtocolSetup<M extends IMessage> {

	private final INetworkEventsHandler<M> handle;

	public ProtocolSetup( INetworkEventsHandler<M> h ){
		handle = h;
	}

	public Tupple<IRunner,IEventQueue<IProtocolEvent<M>>> build( IEventQueue<INetworkEvent> queue ){
		IEventQueue<IProtocolEvent<M>> q = new EventQueue<IProtocolEvent<M>>();
		IRunner run = this.build( queue, q );
		return new Tupple<IRunner,IEventQueue<IProtocolEvent<M>>>( run,q );
	}

	public IRunner build( IEventQueue<INetworkEvent> queue, IEventQueue<IProtocolEvent<M>> outEvents ){
		INetworkEventsHandler<M> eventHandle = new EventHandler<M>( handle );

		MessageMover<M> mover = new MessageMover<M>( eventHandle, queue, outEvents );

		return mover;
	}

}
