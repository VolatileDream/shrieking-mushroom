package protocol.events;

import protocol.IMessage;

public interface IProtoConnectEvent<M extends IMessage> extends
		IProtocolEvent<M> {

}
