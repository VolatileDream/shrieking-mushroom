package sm.protocol.events;

import sm.protocol.IMessage;

public interface IProtoReadEvent<M extends IMessage> extends IProtocolEvent<M> {

	public M getMessage();

}
