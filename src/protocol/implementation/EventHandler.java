package protocol.implementation;

import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetReadEvent;
import networking.events.INetworkEvent;
import protocol.IMessage;
import protocol.INetworkEventsHandler;
import protocol.events.IProtocolEvent;

class EventHandler<M extends IMessage> implements INetworkEventsHandler<M> {
		
	private final INetworkEventsHandler<M> handle;
	
	public EventHandler( INetworkEventsHandler<M> h ){
		handle = h;
	}

	@Override
	public IProtocolEvent<M> handleClose(INetCloseEvent e) {
		return handle.handleClose( e );
	}

	@Override
	public IProtocolEvent<M> handleConnect(INetConnectEvent e) {
		return handle.handleConnect( e );
	}

	@Override
	public IProtocolEvent<M> handleError(INetErrorEvent e) {
		return handle.handleError( e );
	}

	@Override
	public IProtocolEvent<M> handleRead(INetReadEvent e) {
		return handle.handleRead( e );
	}

	@Override
	public IProtocolEvent<M> handleUnknown( INetworkEvent e ) {
		return handle.handleUnknown( e );
	}

}
