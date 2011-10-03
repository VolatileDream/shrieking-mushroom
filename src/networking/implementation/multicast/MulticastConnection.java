package networking.implementation.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;

import networking.implementation.Connection;
import networking.implementation.INetworkIO;
import core.CommonAccessObject;

public class MulticastConnection extends Connection {

	public MulticastConnection( CommonAccessObject cao, int sz, NetworkInterface nif, InetAddress address, int port ) throws IOException {
		super( cao, address, port );
		
		int ttl = cao.handler.GetRequiredVariableAsInt( "networking.multicast.ttl", cao.store );
		
		int soTimeOut = cao.handler.GetRequiredVariableAsInt( "networking.multicast.so_timeout", cao.store );
		
		status = ConnectionStatus.Open;
		
		ioControl = new MulticastIO( nif, address, port, sz, ttl, soTimeOut );
	}

	private INetworkIO ioControl;

	@Override
	public void close() throws IOException {

		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				return;
			}
			status = ConnectionStatus.Changing;
		}

		ioControl.close();

		synchronized( status ){
			status = ConnectionStatus.Closed;
		}
	}

	@Override
	public void flush() throws IOException {

		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				throw new IOException("Multicast Connection isn't open");
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
				throw new IOException("Multicast Connection isn't open");
			}
		}
				
		synchronized( this ){
			
			ioControl.send( m );
			
			lastSent.markCurrentTime();
		}

	}

	@Override
	public byte[] read() throws IOException {
		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				throw new IOException("Multicast Connection isn't open");
			}
		}
		byte[] contents = null;

		synchronized( this ){
			contents = ioControl.read();
		}

		if( contents == null ){
			return null;
		}
		lastReceive.markCurrentTime();
		return contents;
	}

}