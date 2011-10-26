package shriekingMushroom.protocol.implementation;

import shriekingMushroom.networking.events.INetCloseEvent;
import shriekingMushroom.networking.events.INetConnectEvent;
import shriekingMushroom.networking.events.INetErrorEvent;
import shriekingMushroom.networking.events.INetReadEvent;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.INetworkEventsHandler;
import shriekingMushroom.protocol.events.IProtocolEvent;

class EventHandler<M extends IMessage> implements INetworkEventsHandler<M> {

	private final INetworkEventsHandler<M> handle;

	public EventHandler(INetworkEventsHandler<M> h) {
		handle = h;
	}

	@Override
	public IProtocolEvent<M> handleClose(INetCloseEvent e) {
		return handle.handleClose(e);
	}

	@Override
	public IProtocolEvent<M> handleConnect(INetConnectEvent e) {
		return handle.handleConnect(e);
	}

	@Override
	public IProtocolEvent<M> handleError(INetErrorEvent e) {
		return handle.handleError(e);
	}

	@Override
	public IProtocolEvent<M> handleRead(INetReadEvent e) {
		return handle.handleRead(e);
	}

	@Override
	public IProtocolEvent<M> handleUnknown(INetworkEvent e) {
		return handle.handleUnknown(e);
	}

}
