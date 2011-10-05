package networking.implementation.unicast;

import java.io.IOException;
import java.net.Socket;

import networking.implementation.Connection;
import networking.implementation.interfaces.INetworkIO;
import core.CommonAccessObject;

public class UnicastConnection extends Connection {

	private INetworkIO ioControl;

	/**
	 * Creates a Unicast Connection with a socket.
	 * @param s Socket to create the connection with
	 * @param mbSize maximum size the buffer can hold between message reads.
	 * <br>If there are left over bytes between message reads and it can't
	 * hold everything in the buffer it will throw a BufferFullException
	 */
	public UnicastConnection( CommonAccessObject c, Socket soc ){
		super( c, soc.getInetAddress(), soc.getPort() );
		ioControl = new UnicastIO( soc );
		status = ConnectionStatus.Open;
	} 

	@Override
	public void close() throws IOException {
		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				throw new IOException("Unicast Connection isn't open");
			}
			status = ConnectionStatus.Changing;
		}

		try {
			ioControl.close();
		} catch ( IOException e) {
			System.err.println("Issues closing socket to : " + this.getAddress().toString() +"-"+ this.getPort() );
		}
		synchronized( status ){
			status = ConnectionStatus.Closed;
		}
	}

	@Override
	public void flush() throws IOException {

		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				throw new IOException("Unicast Connection isn't open");
			}
		}

		byte[][] tmp = this.getAndClearMessages();

		for( byte[] m : tmp ){
			if( m != null) send( m );
		}

	}

	private void send( byte[] m ) throws IOException {
		
		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				throw new IOException("Unicast Connection isn't open");
			}
		}

		synchronized( ioControl ){
			
			ioControl.send( m );

			lastSent.markCurrentTime();
		}
	}

	@Override
	public byte[] read() throws IOException {
		
		byte[] nb = null;
		
		synchronized( ioControl ){

			nb = ioControl.read();

			if( nb != null ){
				lastReceive.markCurrentTime();
			}
			
		}

		return nb;
	}

}
