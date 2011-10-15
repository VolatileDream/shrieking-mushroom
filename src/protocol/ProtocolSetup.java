package protocol;

import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetReadEvent;
import networking.events.INetworkEvent;
import protocol.events.IProtocolEvent;
import protocol.implementation.EventHandler;
import protocol.implementation.MessageMover;
import protocol.implementation.interfaces.INetworkEventsHandler;
import core.IHandler;
import core.common.Tupple;
import core.events.IEventQueue;
import core.events.implementation.EventQueue;
import core.threading.IRunner;

public class ProtocolSetup<M extends IMessage> {
		
	private final IHandler<IProtocolEvent<M>,INetConnectEvent> connect;
	private final IHandler<IProtocolEvent<M>,INetReadEvent> read;
	private final IHandler<IProtocolEvent<M>,INetErrorEvent> error;
	private final IHandler<IProtocolEvent<M>,INetCloseEvent> close;
	private final IHandler<IProtocolEvent<M>,INetworkEvent> unknown;
	
	public ProtocolSetup(
			IHandler<IProtocolEvent<M>,INetConnectEvent> c,
			IHandler<IProtocolEvent<M>,INetReadEvent> r,
			IHandler<IProtocolEvent<M>,INetErrorEvent> e,
			IHandler<IProtocolEvent<M>,INetCloseEvent> c2,
			IHandler<IProtocolEvent<M>,INetworkEvent> u		
		){
			connect = c;
			read = r;
			error = e;
			close = c2;
			unknown = u;
		}
	
	public
		Tupple<IRunner,IEventQueue<IProtocolEvent<M>>>
	build( IEventQueue<INetworkEvent> queue	){
		IEventQueue<IProtocolEvent<M>> q = new EventQueue<IProtocolEvent<M>>();
		IRunner run = this.build( queue, q );
		return new Tupple<IRunner,IEventQueue<IProtocolEvent<M>>>( run,q );
	}
	
	public IRunner build( IEventQueue<INetworkEvent> queue, IEventQueue<IProtocolEvent<M>> outEvents ){
		INetworkEventsHandler<M> eventHandle = new EventHandler<M>( connect, read, error, close, unknown );
		
		MessageMover<M> mover = new MessageMover<M>( eventHandle, queue, outEvents );
		
		return mover;
	}
		
}
