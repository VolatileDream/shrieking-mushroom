package shriekingMushroom.protocol;

import shriekingMushroom.networking.events.INetCloseEvent;
import shriekingMushroom.networking.events.INetConnectEvent;
import shriekingMushroom.networking.events.INetErrorEvent;
import shriekingMushroom.networking.events.INetReadEvent;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.events.IProtocolEvent;

public interface INetworkEventsHandler<M extends IMessage> {

	public IProtocolEvent<M> handleConnect(INetConnectEvent e);

	public IProtocolEvent<M> handleClose(INetCloseEvent e);

	public IProtocolEvent<M> handleRead(INetReadEvent e);

	public IProtocolEvent<M> handleError(INetErrorEvent e);

	public IProtocolEvent<M> handleUnknown(INetworkEvent e);

}
