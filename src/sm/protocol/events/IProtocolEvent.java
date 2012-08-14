package sm.protocol.events;

import shriekingmushroom.events.IEvent;
import sm.protocol.IMessage;
import sm.protocol.IProtocolConnection;

public interface IProtocolEvent<M extends IMessage> extends IEvent {

	public IProtocolConnection<M> getConnection();
}
