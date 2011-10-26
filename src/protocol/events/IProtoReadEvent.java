package protocol.events;

import protocol.IMessage;

public interface IProtoReadEvent<M extends IMessage> extends IProtocolEvent<M> {

	public M getMessage();

}
