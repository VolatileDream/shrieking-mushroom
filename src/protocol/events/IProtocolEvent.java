package protocol.events;

import protocol.IMessage;
import protocol.IProtocolConnection;
import core.events.IEvent;

public interface IProtocolEvent<M extends IMessage> extends IEvent {

	public IProtocolConnection<M> getConnection();
}
