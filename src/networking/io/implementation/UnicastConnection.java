package networking.io.implementation;

import java.io.IOException;
import java.net.Socket;

import networking.exceptions.BufferFullException;
import networking.exceptions.InsufficientMessageLengthException;
import networking.exceptions.MalformedMessageException;
import networking.io.IMessage;
import networking.io.IMessageFactory;
import networking.io.IMessageStream;
import networking.io.INetworkIO;
import networking.io.IMessageStream.ConnectionStatus;
import core.Tupple;
import core.uservars.IVariableHandler;
import core.uservars.IVariableStore;

public class UnicastConnection extends Connection implements IMessageStream {

	private final Socket soc;
	private final int maxBufferSize;
	private byte[] buffer;

	private INetworkIO ioControl;

	/**
	 * Creates a Unicast Connection with a socket.
	 * @param s Socket to create the connection with
	 * @param mbSize maximum size the buffer can hold between message reads.
	 * <br>If there are left over bytes between message reads and it can't
	 * hold everything in the buffer it will throw a BufferFullException
	 */
	public UnicastConnection( IVariableStore store, IMessageFactory mFact, IVariableHandler handle, Socket s, int mbSize ){
		super( store, mFact, handle );
		soc = s;
		if( mbSize == -1 ){
			maxBufferSize = Integer.MAX_VALUE;
		}else if( mbSize < 0 ){
			maxBufferSize = 0;
		}else{
			maxBufferSize = mbSize;
		}
		buffer = new byte[ 0 ];
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
			soc.close();
		} catch ( IOException e) {
			System.err.println("Issues closing socket to : " + soc.getInetAddress().toString() +"-"+soc.getPort() );
		}
		synchronized( status ){
			status = ConnectionStatus.Closed;
		}
	}

	@Override
	public boolean express(IMessage m) throws IOException {

		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				throw new IOException("Unicast Connection isn't open");
			}
		}

		synchronized( soc ){
			send( m );
		}
		return true;
	}

	@Override
	public void flush() throws IOException {

		synchronized( status ){
			if( status != ConnectionStatus.Open ){
				throw new IOException("Unicast Connection isn't open");
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
				throw new IOException("Unicast Connection isn't open");
			}
		}

		synchronized( soc ){
			byte[] message = msgFactory.toBytes( m );
			ioControl.send(message);
			
			lastSent.markCurrentTime();
		}
	}

	@Override
	public IMessage read() throws BufferFullException, IOException {
		IMessage msg = null;

		synchronized( buffer  ){

			synchronized( soc ){

				byte[] nb = ioControl.read();

				if( nb != null && nb.length > 0 ){
										
					buffer = core.Util.add(buffer, nb);
					
					lastReceive.markCurrentTime();
					
				}
			}

			try {
				
				boolean isTortured = treatStreamAsTortured();
				
				Tupple<IMessage,Integer> msgAndByteReadPair = msgFactory.fromBytes( buffer, isTortured );
				
				msg = msgAndByteReadPair.Item1;
				
				if( msgAndByteReadPair.Item2 > 0){
					buffer = core.Util.shift( msgAndByteReadPair.Item2, buffer);
				}
			
			} catch (MalformedMessageException e) {
				// how say : kinda screwed?
				fixAndClearBuffer();
			} catch (InsufficientMessageLengthException e) {
				//TODO
				msg = null;
				e.printStackTrace();
			}

			if( buffer.length > maxBufferSize ){
				System.err.write( buffer );
				buffer = new byte[0];
				throw new BufferFullException("Buffer is larger then it's maximum allowed size, buffer has been reset, and it's contents logged");
			}

		}
		return msg;
	}

	private void fixAndClearBuffer(){
		synchronized( buffer ){

			boolean isTortured = treatStreamAsTortured();

			int firstIndex = msgFactory.getFirstValidMessageStart( buffer, isTortured );

			byte[] newBuffer = null;
			if( firstIndex == -1 ){
				newBuffer = new byte[0];
			}else{
				newBuffer = new byte[buffer.length - firstIndex];
				System.arraycopy(buffer, firstIndex, newBuffer, 0, newBuffer.length );
			}
			buffer = newBuffer;
		}

	}
	
	private boolean treatStreamAsTortured(){
		boolean isTortured = varHandler.GetRequiredVariableAsBoolean( "networking.messages.treat_as_tortured", varStore);
		return isTortured;
	}

}
