package core;

import java.io.IOException;
import java.net.InetAddress;

import networking.TCPConnectionBuilder;
import networking.TCPNetworkAccess;
import networking.events.INetworkEvent;
import networking.implementation.tcp.TCPConnectionPool;
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
import core.threading.IRunner;

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
		
		TCPNetworkAccess netAccess = new TCPConnectionPool( cao );
		
		TCPConnectionBuilder netF = new TCPConnectionBuilder( netAccess );
		
		netF.withPort( 54444 );
		IEventQueue<INetworkEvent> eq = new EventQueue<INetworkEvent>();
		netF.withQueue( eq );
		
		// Protocol Layer setup
		
		MessageMover mover = ProtocolSetup.setup( cao, eq );
		
		// Actual usage Setup
		
		//protocol layer start
		mover.start();
		
		//networking listen
		IRunner tcp54444 = netF.allowConnection();
		tcp54444.start();

		//networking connect 
		InetAddress adr = InetAddress.getByName("Po.local");
		netF.connect( adr );
		
		sleep(5000);
		
		
		
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
