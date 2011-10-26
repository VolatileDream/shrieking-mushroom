package protocol;

import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetReadEvent;
import networking.events.INetworkEvent;
import protocol.events.IProtocolEvent;

public interface INetworkEventsHandler<M extends IMessage> {

	public IProtocolEvent<M> handleConnect(INetConnectEvent e);

	public IProtocolEvent<M> handleClose(INetCloseEvent e);

	public IProtocolEvent<M> handleRead(INetReadEvent e);

	public IProtocolEvent<M> handleError(INetErrorEvent e);

	public IProtocolEvent<M> handleUnknown(INetworkEvent e);

}
