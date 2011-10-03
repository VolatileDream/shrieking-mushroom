package protocol.implementation;

import networking.events.INetworkEvent;
import protocol.IMessageFactory;
import protocol.IMessageFilter;
import protocol.ITranslator;
import protocol.implementation.interfaces.MyMessage;
import core.CommonAccessObject;
import core.events.IEventQueue;

public class ProtocolSetup {
	
	
	public static MessageMover setup( CommonAccessObject c, IEventQueue<INetworkEvent> allEventsQueue ){
		MessageType text = new MessageType("text");
		MessageType con = new MessageType("connection");
		MessageType redund = new MessageType("redundancy");
		
		IMessageFactory<MyMessage> f = new MessageFactory( c.log, text, con, redund );
		ITranslator<MyMessage> clientTr = new Translator( c, allEventsQueue, f );
		IMessageFilter<MyMessage> filter = new MessageFilter();
		MessageMover mover = new MessageMover( filter, clientTr );
		
		return mover;
	}
	
}
