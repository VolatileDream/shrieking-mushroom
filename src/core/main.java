package core;

import java.io.IOException;
import java.net.InetAddress;

import networking.TCPNetworkAccess;
import networking.TCPServer;
import networking.implementation.unicast.TCPConnectionPool;
import protocol.implementation.MessageMover;
import protocol.implementation.ProtocolSetup;
import core.config.IVariable;
import core.config.IVariableHandler;
import core.config.IVariableStore;
import core.config.implementation.UserVariableHandler;
import core.config.implementation.def.DefaultNetworkingVariableStore;
import core.events.IEventQueue;
import core.events.implementation.EventQueue;
import core.logging.ILogger;
import core.logging.LocalTextLogger;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		
		// General Setup
		
		IVariableStore store = new DefaultNetworkingVariableStore();
		IVariableHandler handler = new UserVariableHandler();
		
		IVariable logFile = handler.GetRequiredVariable( "logging.logFile", store );
		int logFlag = handler.GetRequiredVariableAsInt( "logging.logProfile", store );
		
		ILogger log = new LocalTextLogger( logFile.GetValue(), logFlag );
		
		CommonAccessObject cao = new CommonAccessObject( store, handler, log );
		
		// Network Layer Setup
		
		IEventQueue eq = new EventQueue();
		
		TCPNetworkAccess netAccess = new TCPConnectionPool( cao, eq, eq );// use same event queue for both

		// Protocol Layer setup
		
		MessageMover mover = ProtocolSetup.setup( cao, eq );
		
		// Actual usage Setup
		
		//protocol layer start
		mover.start();
		
		//networking listen
		TCPServer tcp54444 = netAccess.allowConnection( 54444 );
		tcp54444.start();

		//networking connect 
		InetAddress adr = InetAddress.getByName("Po.local");
		netAccess.connect( adr, 54444 );
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {}
		
		//networking close
		tcp54444.stop();
		netAccess.close();
		
		System.out.println("DONE");
		
	}

	private static void sleep( long i ){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e1) {}
	}
	
}
