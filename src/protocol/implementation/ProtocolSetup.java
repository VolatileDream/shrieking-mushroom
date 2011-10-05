package protocol.implementation;

import networking.events.INetworkEvent;
import protocol.IMessageFactory;
import protocol.INetworkEventsHandler;
import protocol.events.IProtocolEvent;
import protocol.implementation.interfaces.MyMessage;
import core.CommonAccessObject;
import core.events.IEventQueue;
import core.events.implementation.EventQueue;

public class ProtocolSetup {
	
	
	public static MessageMover setup( CommonAccessObject c, IEventQueue<INetworkEvent> allEventsQueue ){
		MessageType text = new MessageType("text");
		MessageType con = new MessageType("connection");
		MessageType redund = new MessageType("redundancy");
		
		IEventQueue<IProtocolEvent<MyMessage>> outEvents = new EventQueue<IProtocolEvent<MyMessage>>();
		
		IMessageFactory<MyMessage> f = new MessageFactory( c.log, text, con, redund );
		
		INetworkEventsHandler eventHandle = new EventHandler( c, outEvents, f );
		
		MessageMover mover = new MessageMover( eventHandle, allEventsQueue );
		
		return mover;
	}
	
}
