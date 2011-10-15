package protocol.implementation;

import core.IHandler;
import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetReadEvent;
import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.events.IProtocolEvent;
import protocol.implementation.interfaces.INetworkEventsHandler;

public class EventHandler<M extends IMessage> implements INetworkEventsHandler<M> {
		
	private final IHandler<IProtocolEvent<M>,INetConnectEvent> connect;
	private final IHandler<IProtocolEvent<M>,INetReadEvent> read;
	private final IHandler<IProtocolEvent<M>,INetErrorEvent> error;
	private final IHandler<IProtocolEvent<M>,INetCloseEvent> close;
	private final IHandler<IProtocolEvent<M>,INetworkEvent> unknown;
	
	public EventHandler(
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

	@Override
	public IProtocolEvent<M> handleClose(INetCloseEvent e) {
		return close.handle( e );
		//table.remove( e.getConnection() );
	}

	@Override
	public IProtocolEvent<M> handleConnect(INetConnectEvent e) {
		return connect.handle( e );
		//table.put( e.getConnection(), new ConnectionInfo() );
	}

	@Override
	public IProtocolEvent<M> handleError(INetErrorEvent e) {
		return error.handle( e );
	}

	@Override
	public IProtocolEvent<M> handleRead(INetReadEvent e) {
		return read.handle( e );
		/*
		IConnection con = e.getConnection();
		ConnectionInfo info = table.get( con );
		if( info == null ){
			cao.log.Log( "Couldn't find connection info for connection", LogLevel.Error );
			info = new ConnectionInfo();
			table.put( e.getConnection(), info );
		}
		info.buffer = Util.concat( info.buffer, e.getRead() );
		Tupple<MyMessage,Integer> result = msgFactory.transformToMessage( con, info.buffer );
		
		if( result.Item2 > 0 ){
			//shift the buffer if we get a non zero return, they might want to clean it.
			info.buffer = Util.shift( result.Item2, info.buffer );
		}
		
		if( result.Item1 == null ){
			return;
		}
		
		IProtoReadEvent<MyMessage> event = new ProtocolReadEvent<MyMessage>( e, conFactory.transform( con ), result.Item1 );
		queue.offer( event );
		*/
	}

	@Override
	public IProtocolEvent<M> handleUnknown( INetworkEvent e ) {
		return unknown.handle( e );
	}

}
