package protocol;

import protocol.events.IProtocolEvent;
import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetworkEvent;
import networking.events.INetReadEvent;

public interface INetworkEventsHandler<M extends IMessage> {
	
	public IProtocolEvent<M> handleConnect( INetConnectEvent e );
	
	public IProtocolEvent<M> handleClose( INetCloseEvent e );
	
	public IProtocolEvent<M> handleRead( INetReadEvent e );
	
	public IProtocolEvent<M> handleError( INetErrorEvent e );
	
	public IProtocolEvent<M> handleUnknown( INetworkEvent e );
	
}
