package core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import networking.io.ConnectionFactory;
import networking.io.IMessageStream;
import networking.io.IMessage;
import networking.io.IMessageFactory;
import networking.io.MessageType;
import networking.io.implementation.Message;
import networking.io.implementation.MessageFactory;
import core.logging.LocalTextLogger;
import core.uservars.IVariableHandler;
import core.uservars.IVariableStore;
import core.uservars.implementation.DefaultVariableStore;
import core.uservars.implementation.UserVariableHandler;


public class TestFunctionality {

	private static MessageType textMessage = new MessageType("Text");
	
	public static void main( String[] args ) throws IOException{
				
		String interfaceName = "eth2";
		
		
		
		Message msg = new Message("O HAI <> :D", textMessage );
		
		IVariableStore store = new DefaultVariableStore();
		IMessageFactory mFact = new MessageFactory( new LocalTextLogger("res/logging.txt",31), textMessage );
		IVariableHandler vHandle= new UserVariableHandler();
		
		ConnectionFactory conn = new ConnectionFactory( store, mFact, vHandle );
		
		try {
			InetAddress adrs = InetAddress.getByName("localhost");
			int port = 7893;
			
			showNetInterfaces();
			NetworkInterface nif = NetworkInterface.getByName(interfaceName);
			
			runOtherUni( 2000, port, nif, msg, conn );
			
			IMessageStream connect = conn.ConstructUnicastClient(adrs, port);
			
			IMessage m = null;
			while( m == null ){
				m = connect.read();
				try {
					Thread.sleep(200);
				} catch (Exception e) {}
			}
			System.out.println( new String( m.getContents() ) );
			
			
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//*
		
		
		
		try {
			InetAddress adrs = InetAddress.getByName("228.5.6.8");
			int port = 7893;
			
			showNetInterfaces();
			NetworkInterface eth2 = NetworkInterface.getByName("eth2");
			
			System.out.println( adrs.getHostName() +" : "+ port );

			runOtherMulti(adrs, port, eth2, conn );
			
			IMessageStream connection2 = conn.ConstructMulticast( eth2, adrs, port );
			long startTime = System.currentTimeMillis();
			while( System.currentTimeMillis() - startTime < 2000 ){
				connection2.express(msg);
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			connection2.close();
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//*/
	}
	
	public static void showNetInterfaces(){
		
		try {
			Enumeration<NetworkInterface> nif = NetworkInterface.getNetworkInterfaces();
			while( nif.hasMoreElements() ){
				
				NetworkInterface n = nif.nextElement();
				System.out.println("Interface : "+ n.getDisplayName() );
			}
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void runOtherMulti(final InetAddress addresses,final int port,final NetworkInterface nif, final ConnectionFactory conn){
		Thread t = new Thread(){
			
			public void run(){
				try {
					
					IMessageStream connection2 = conn.ConstructMulticast( nif, addresses, port );
					IMessage m = null;
					do{
						m = connection2.read();
						if( m != null ){
							System.out.println( new String( m.getContents() ) );
						}
					} while( m != null );
					connection2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		};
		t.setDaemon(true);
		t.start();
		
	}
		
	public static void runOtherUni(final int wait,final int port,final NetworkInterface nif,final Message msg, final ConnectionFactory conn ){
		Thread t = new Thread(){
			
			public void run(){
				try {
					IMessageStream connection2 = conn.ConstructUnicastServer(nif, port, wait );
					for( int i=0; i < 1; i ++){
						connection2.addMessage( msg );
					}
					connection2.flush();
					connection2.close();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
		t.setDaemon(true);
		t.start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
