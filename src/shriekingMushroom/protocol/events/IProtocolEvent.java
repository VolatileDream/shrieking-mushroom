package shriekingMushroom.protocol.events;

import shriekingMushroom.core.events.IEvent;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.IProtocolConnection;

public interface IProtocolEvent<M extends IMessage> extends IEvent {

	public IProtocolConnection<M> getConnection();
}
