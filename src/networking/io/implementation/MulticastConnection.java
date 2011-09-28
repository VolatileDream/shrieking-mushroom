package networking.io.implementation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;

import networking.exceptions.InsufficientMessageLengthException;
import networking.exceptions.MalformedMessageException;
import networking.io.IMessage;
import networking.io.IMessageFactory;
import networking.io.IMessageStream;
import networking.io.INetworkIO;
import core.uservars.IVariableHandler;
import core.uservars.IVariableStore;


public class MulticastConnection extends Connection implements IMessageStream {

	public MulticastConnection( IVariableStore store, IMessageFactory fac, IVariableHandler handle, int sz, NetworkInterface nif, InetAddress address, int port ) throws IOException {
		super( store, fac, handle );
		
		int ttl = varHandler.GetRequiredVariableAsInt( "networking.multicast.ttl", varStore );
		
		int soTimeOut = varHandler.GetRequiredVariableAsInt( "networking.multicast.so_timeout", varStore );
		
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
	public boolean express( IMessage m) throws IOException {

		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				return false;
			}
		}
		this.send( m );

		return true;
	}

	@Override
	public void flush() throws IOException {

		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				throw new IOException("Multicast Connection isn't open");
			}
		}

		IMessage[] tmp = this.getAndClearMessages();

		for( IMessage m : tmp ){
			if( m != null) send( m );
		}

	}

	private void send( IMessage m ) throws IOException {

		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				throw new IOException("Multicast Connection isn't open");
			}
		}
				
		synchronized( this ){
			
			byte[] send = msgFactory.toBytes(m);
			
			ioControl.send( send );
			
			lastSent.markCurrentTime();
		}

	}

	@Override
	public IMessage read() throws IOException {
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
		
		IMessage result = null;
		try {
			// is tortured because it's multicast, could be crap before our message
			result = msgFactory.fromBytes( contents, true ).Item1;
		} catch ( MalformedMessageException e) {
			result = null;
			e.printStackTrace();
		} catch ( InsufficientMessageLengthException e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

}