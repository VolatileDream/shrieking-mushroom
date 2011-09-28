package networking.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import networking.io.implementation.MulticastConnection;
import networking.io.implementation.UnicastConnection;

import core.uservars.IVariableHandler;
import core.uservars.IVariableStore;

public class ConnectionFactory {

	private final IVariableStore store;
	private final IMessageFactory msgFactory;
	private final IVariableHandler varHandler;
	
	public ConnectionFactory( IVariableStore vstore, IMessageFactory mfac, IVariableHandler h ){
		this.store = vstore;
		this.msgFactory = mfac;
		this.varHandler = h;
	}
	
	public IMessageStream ConstructUnicastClient( InetAddress inet, int port)
		throws SocketException{
		
		String varBufSz = "networking.unicast.buffer_size";
		
		UnicastConnection connect= null;
		try {
			
			Socket soc = new Socket( inet, port );
			soc.setKeepAlive( true );
			
			int bufSz = varHandler.GetRequiredVariableAsInt(varBufSz, store);
			
			connect = new UnicastConnection( store, msgFactory, varHandler, soc, bufSz );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connect;
	}

	public IMessageStream ConstructUnicastServer( NetworkInterface nif, int port, int wait )
		throws SocketException {

		if( !nif.isUp() ){
			return null;
		}
		
		try {
			
			UnicastConnection connect = null;

			int bufSz = varHandler.GetRequiredVariableAsInt("networking.unicast.buffer_size", store);
			
			ServerSocket soc = new ServerSocket( port );
			soc.setSoTimeout( wait );
			
			Socket connectionSocket = soc.accept();
			connectionSocket.setKeepAlive( true );
			
			connect = new UnicastConnection( store, msgFactory, varHandler, connectionSocket, bufSz );
					
			return connect;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public IMessageStream ConstructMulticast( NetworkInterface nif, InetAddress address, int port )
		throws SocketException, IOException {
		
		if( !nif.supportsMulticast() ){
			throw new IOException("Interface " + nif.getDisplayName() +" does not support multicast");
		}
		if( ! nif.isUp() ){
			throw new IOException("Interface "+ nif.getDisplayName()+" is not up at the moment");
		}
		
		MulticastConnection connect = null;
		try {
			
			int bufSz = varHandler.GetRequiredVariableAsInt("networking.multicast.packet_size", store);
			
			connect = new MulticastConnection( store, msgFactory, varHandler, bufSz, nif, address, port );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connect;
	}

}
