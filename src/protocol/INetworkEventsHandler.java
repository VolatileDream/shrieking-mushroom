package protocol;

import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetworkEvent;
import networking.events.INetReadEvent;

public interface INetworkEventsHandler {
	
	public void handleConnect( INetConnectEvent e );
	
	public void handleClose( INetCloseEvent e );
	
	public void handleRead( INetReadEvent e );
	
	public void handleError( INetErrorEvent e );
	
	public void handleUnknown( INetworkEvent e );
	
}
