package demoApp;

import java.io.IOException;
import java.net.InetAddress;

import networking.TCPConnectionBuilder;
import networking.TCPNetworkAccess;
import networking.events.INetworkEvent;
import networking.implementation.tcp.TCPConnectionPool;
import protocol.IMessageFactory;
import protocol.INetworkEventsHandler;
import protocol.IProtocolConnection;
import protocol.events.IProtoConnectEvent;
import protocol.events.IProtocolEvent;
import protocol.implementation.ProtocolSetup;
import core.CommonAccessObject;
import core.config.IVariable;
import core.config.IVariableHandler;
import core.config.IVariableStore;
import core.config.implementation.JointVariableStore;
import core.config.implementation.UserVariableHandler;
import core.config.implementation.def.DefaultNetworkingVariableStore;
import core.config.implementation.def.DefaultProtocolVariableStore;
import core.events.IEventQueue;
import core.events.implementation.EventQueue;
import core.logging.ILogger;
import core.logging.implementation.JoinedLogger;
import core.logging.implementation.LocalTextLogger;
import core.logging.implementation.StandardErrorLogger;
import core.threading.IRunner;
import core.threading.IWaiter;
import core.threading.implementation.CommonWaitTime;
import demoApp.protocol.Handlers;
import demoApp.protocol.Message;
import demoApp.protocol.MessageFactory;
import demoApp.protocol.MessageType;
import demoApp.protocol.interfaces.MyMessage;

public class DemoApp {

	public static void doDemo() throws IOException {
		
		//*
		
		System.out.println("Starting demo");
		
		// General Setup
		
		DefaultNetworkingVariableStore netStore = new DefaultNetworkingVariableStore();
		DefaultProtocolVariableStore protoStore = new DefaultProtocolVariableStore();
		IVariableStore store = new JointVariableStore( netStore, protoStore );
		IVariableHandler handler = new UserVariableHandler();
		
		IVariable logFile = handler.GetRequiredVariable( "logging.logFile", store );
		int logFlag = handler.GetRequiredVariableAsInt( "logging.logProfile", store );
		
		ILogger log1 = new LocalTextLogger( logFile.GetValue(), logFlag );
		ILogger log2 = new StandardErrorLogger( logFlag );
		
		ILogger log = new JoinedLogger( log1, log2 );
		
		CommonAccessObject cao = new CommonAccessObject( store, handler, log );
		
		System.out.println("General Setup completed...");
		
		// Network Layer Setup
		
		long netSleep = cao.handler.GetRequiredVariableAsInt("threading.default_sleep_millis", cao.store);
		
		IWaiter netWait = new CommonWaitTime( netSleep );
		
		TCPNetworkAccess netAccess = new TCPConnectionPool( cao, netWait );
				
		TCPConnectionBuilder netF = new TCPConnectionBuilder( netAccess );
		
		netF.withPort( 54444 );
		IEventQueue<INetworkEvent> nQueue = new EventQueue<INetworkEvent>();
		netF.withQueue( nQueue );
		
		System.out.println("Network layer setup completed...");
		
		// Protocol Layer setup

		MessageType text = new MessageType("TEXT");
		MessageType con = new MessageType("CONNECTION");
		MessageType data = new MessageType("DATA");
		
		IMessageFactory<MyMessage> msgF = new MessageFactory( cao.log, text, con, data );
		
		INetworkEventsHandler<MyMessage> pHandler = new Handlers( cao, msgF );
		
		ProtocolSetup<MyMessage> pSetup = new ProtocolSetup<MyMessage>( cao, pHandler );
		IEventQueue<IProtocolEvent<MyMessage>> pQueue = new EventQueue<IProtocolEvent<MyMessage>>();
		IRunner mover = pSetup.build( nQueue, pQueue );
		
		System.out.println("Protocol layer setup completed...");
		
		// Actual usage Setup
		
		//protocol layer start
		mover.start();
		
		System.out.println("Protocol layer started...");
		
		//networking listen
		IRunner tcp54444 = netF.allowConnection();
		tcp54444.start();

		System.out.println("Server started...");
		
		//networking connect 
		InetAddress adr = InetAddress.getLocalHost();
		netF.connect( adr );
		
		System.out.println("Client started...");
		
		long timeOut = 10*1000;//one minute
		
		long start = System.currentTimeMillis();
		long current = 0;
		
		System.out.println("Starting to wait");
		
		while( start + timeOut > current ){
			current = System.currentTimeMillis();
			
			if ( pQueue.poll() ) {
				
				IProtocolEvent<MyMessage> event = pQueue.remove();
				if (event instanceof IProtoConnectEvent<?>) {

					IProtoConnectEvent<MyMessage> pc = (IProtoConnectEvent<MyMessage>) event;
					IProtocolConnection<MyMessage> c = pc.getConnection();
					System.out.println("Connected to: " + c.getAddress() + ":"
							+ c.getPort());
					
					//MyMessage m = new Message( null, null, text );
					
				}else{
					
					System.out.println( event.getClass() );
					
				}
			}
			sleep(200);
		}
		
		System.out.println("Done waiting");
		
		//networking close
		tcp54444.stop();
		netAccess.close();
		
		System.out.println("networking layer shutdown...");
		
		mover.stop();
		
		System.out.println("Protocol layer shutdown...");
		//*/
		
		System.out.println("DONE DEMO");
		
	}

	
	private static void sleep( long i ){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e1) {}
	}
	
}
