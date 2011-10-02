package core;

import java.io.IOException;
import java.net.InetAddress;

import networking.TCPConnectionPool;
import networking.interfaces.TCPNetworkAccess;
import networking.interfaces.TCPServer;
import core.config.IVariable;
import core.config.IVariableHandler;
import core.config.IVariableStore;
import core.config.implementation.UserVariableHandler;
import core.config.implementation.def.DefaultNetworkingVariableStore;
import core.events.IConnectEvent;
import core.events.IEvent;
import core.events.IEventQueue;
import core.events.IReadEvent;
import core.events.implementation.EventQueue;
import core.logging.ILogger;
import core.logging.LocalTextLogger;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		
		IVariableStore store = new DefaultNetworkingVariableStore();
		IVariableHandler handler = new UserVariableHandler();
		
		IVariable logFile = handler.GetRequiredVariable( "logging.logFile", store );
		int logFlag = handler.GetRequiredVariableAsInt( "logging.logProfile", store );
		
		ILogger log = new LocalTextLogger( logFile.GetValue(), logFlag );
		
		CommonAccessObject cao = new CommonAccessObject( store, handler, log );
		
		IEventQueue eq = new EventQueue();
		
		TCPNetworkAccess netAccess = new TCPConnectionPool( cao, eq, eq );// use same event queue for both
		
		TCPServer tcp54444 = netAccess.allowConnection( 54444 );
		tcp54444.start();

		InetAddress adr = InetAddress.getByName("Po.local");
		try {
			Thread.sleep(5000);
			System.out.println("Almost done waiting...");
			Thread.sleep(2000);
		} catch (InterruptedException e1) {}
		netAccess.connect( adr, 54444 );
		
		eq.waitFor();
		
		System.out.println("Done waiting for queue");
		
		while( eq.poll() ){
			
			IEvent e = eq.remove();
			System.out.print( e.getClass().getName() +" "+e.getConnection().getAddress().toString()+":");
			
			//System.out.println( e.getClass().getName() );
			
			if( e instanceof IConnectEvent ){
				e.getConnection().write( "O Hai :)".getBytes() );
			}else if( e instanceof IReadEvent ){
				IReadEvent re = (IReadEvent)e;
				System.out.print( new String( re.getRead() ) );
			}
			System.out.println();
			sleep(200);
			
		}
		
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
