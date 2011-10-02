package networking.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;

import networking.io.implementation.MulticastConnection;
import networking.io.implementation.UnicastConnection;
import core.CommonAccessObject;

public class ConnectionFactory {

	private final CommonAccessObject cao;


	public ConnectionFactory( CommonAccessObject c ){
		cao = c;
	}

	/**
	 * Attempts to connect to the host at inet, on port port.
	 * @param inet The address of the server
	 * @param port The port to connect to
	 * @return Returns an object that implements the InternalConnection interface
	 * @throws SocketException If the socket throws an exception during connection
	 */

	public InternalConnection ConstructUnicastClient( InetAddress inet, int port)
			throws SocketException, IOException {

		UnicastConnection connect= null;
		
		Socket soc = new Socket( inet, port );
		soc.setKeepAlive( true );

		connect = new UnicastConnection( cao, soc );

		return connect;
	}

	public InternalConnection ConstructMulticast( NetworkInterface nif, InetAddress address, int port )
			throws SocketException, IOException {

		if( !nif.supportsMulticast() ){
			throw new IOException("Interface " + nif.getDisplayName() +" does not support multicast");
		}
		if( ! nif.isUp() ){
			netInterfaceNotUp(nif);
		}

		MulticastConnection connect = null;
		try {

			int bufSz = cao.handler.GetRequiredVariableAsInt("networking.multicast.packet_size", cao.store );

			connect = new MulticastConnection( cao, bufSz, nif, address, port );

		} catch (Exception e) {
			e.printStackTrace();
		}
		return connect;
	}

	private void netInterfaceNotUp( NetworkInterface nif ) throws IOException {
		throw new IOException("Interface "+ nif.getDisplayName()+" is not up at the moment");
	}

}
