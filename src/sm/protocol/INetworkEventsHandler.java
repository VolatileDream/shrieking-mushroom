package sm.protocol;

import sm.networking.events.INetCloseEvent;
import sm.networking.events.INetConnectEvent;
import sm.networking.events.INetErrorEvent;
import sm.networking.events.INetReadEvent;
import sm.networking.events.INetworkEvent;
import sm.protocol.events.IProtocolEvent;

public interface INetworkEventsHandler<M extends IMessage> {

	public IProtocolEvent<M> handleConnect(INetConnectEvent e);

	public IProtocolEvent<M> handleClose(INetCloseEvent e);

	public IProtocolEvent<M> handleRead(INetReadEvent e);

	public IProtocolEvent<M> handleError(INetErrorEvent e);

	public IProtocolEvent<M> handleUnknown(INetworkEvent e);

}
