package shriekingMushroom.protocol.events;

import shriekingMushroom.protocol.IMessage;

public interface IProtoReadEvent<M extends IMessage> extends IProtocolEvent<M> {

	public M getMessage();

}
