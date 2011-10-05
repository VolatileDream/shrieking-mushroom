package protocol.implementation;

import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetworkEvent;
import networking.events.INetReadEvent;
import protocol.IMessageFactory;
import protocol.INetworkEventsHandler;
import protocol.events.IProtocolEvent;
import protocol.implementation.interfaces.MyMessage;
import core.CommonAccessObject;
import core.events.IEventQueue;
import core.logging.ILogger.LogLevel;

public class EventHandler implements INetworkEventsHandler {

	private final CommonAccessObject cao;
	private final IEventQueue<IProtocolEvent<MyMessage>> queue;
	private final IMessageFactory<MyMessage> factory;
	
	public EventHandler( CommonAccessObject c, IEventQueue<IProtocolEvent<MyMessage>> q, IMessageFactory<MyMessage> f ){
		queue = q;
		cao = c;
		factory = f;
	}

	@Override
	public void handleClose(INetCloseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleConnect(INetConnectEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleError(INetErrorEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRead(INetReadEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleUnknown( INetworkEvent e ) {
		cao.log.Log( "Unknown INetworkEvent type: "+ e.getClass().getName(), LogLevel.Warn );
	}

}
