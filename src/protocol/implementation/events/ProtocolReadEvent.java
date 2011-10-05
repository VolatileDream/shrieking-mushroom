package protocol.implementation.events;

import networking.events.INetworkEvent;
import protocol.IProtocolConnection;
import protocol.events.IProtoReadEvent;
import protocol.implementation.interfaces.MyMessage;

public class ProtocolReadEvent extends ProtocolEvent implements IProtoReadEvent<MyMessage> {

	private final MyMessage message;
	
	public ProtocolReadEvent(INetworkEvent e, IProtocolConnection<MyMessage> c, MyMessage m ) {
		super(e, c);
		message = m;
	}

	@Override
	public MyMessage getMessage() {
		return message;
	}

}
