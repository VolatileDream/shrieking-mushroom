package protocol.implementation;

import java.util.Hashtable;

import networking.IConnection;
import protocol.IMessageFactory;
import protocol.ITranslator;
import protocol.implementation.interfaces.MyMessage;
import core.CommonAccessObject;
import core.Tupple;
import core.events.ICloseEvent;
import core.events.IConnectEvent;
import core.events.IErrorEvent;
import core.events.IEvent;
import core.events.IEventQueue;
import core.events.IReadEvent;
import core.logging.ILogger.LogLevel;

public class Translator implements ITranslator<MyMessage> {

	private final IEventQueue queue;
	private final CommonAccessObject cao;
	private final IMessageFactory<MyMessage> factory;
	private final Hashtable<IConnection, byte[]> buffers = new Hashtable<IConnection,byte[]>();
	
	public Translator( CommonAccessObject c, IEventQueue e, IMessageFactory<MyMessage> f ){
		queue = e;
		cao = c;
		factory = f;
	}

	public synchronized MyMessage getMessage(){

		dealWithEvents();
		MyMessage msg = getMessage1();
		
		if( msg != null ){
			return msg;
		}
		
		do{
			queue.waitFor();
			dealWithEvents();
		}while( (msg = getMessage1()) == null);
		return msg;
		
	}

	private synchronized MyMessage getMessage1(){
		MyMessage msg = null;
		
		for( IConnection c : buffers.keySet() ){
			
			byte[] buf = buffers.get( c );
			
			Tupple<MyMessage,Integer> result = factory.transformToMessage( c, buf ); 
			
			if( result.Item1 != null && result.Item2 > 0 ){
				
				byte[] tmp = new byte[ buf.length - result.Item2 ];
				System.arraycopy(buf, 0, tmp, 0, result.Item2 );
				
				buffers.put( c, tmp );
				msg = result.Item1;
				break;
			}
		}
		return msg;
	} 
	
	private synchronized void dealWithEvents(){
		
		while( queue.poll() ){
			
			IEvent event = queue.remove();

			if( event instanceof IConnectEvent ){
				connectEvent( (IConnectEvent) event );

			}else if( event instanceof ICloseEvent ){
				closeEvent( (ICloseEvent) event ); 

			}else if( event instanceof IErrorEvent ){
				errorEvent( (IErrorEvent) event );

			}else if( event instanceof IReadEvent ){
				readEvent( (IReadEvent) event );

			}else{
				//TODO what do?
			}

		}
	}
	
	private synchronized void connectEvent( IConnectEvent e ){
		IConnection c = e.getConnection();
		boolean in = buffers.containsKey( c );

		if( in ){
			//problem, have to reset the buffer.
			cao.log.Log(
					"Translator buffer contains a previous entry for this connection ( "
					+ formatConnection( c )
					+") perhaps connections aren't being cleaned up after?",
					LogLevel.Warn
			);
		}

		buffers.put( c, new byte[0] );
	}

	private synchronized void closeEvent( ICloseEvent e ){
		buffers.remove( e.getConnection() );
	}

	private synchronized void errorEvent( IErrorEvent e ){
		//TODO what do?
	}

	private synchronized void readEvent( IReadEvent e ){
		IConnection c = e.getConnection();
		byte[] buffer = e.getRead();

		byte[] old = buffers.get( c );
		if( old == null ){
			cao.log.Log(
					"Translator does not contain an entry for this connection ("+formatConnection(c)+")",
					LogLevel.Error
			);
			old = buffer;
		}else{
			byte[] tmp = new byte[ old.length + buffer.length ];
			System.arraycopy(old, 0, tmp, 0, old.length);
			System.arraycopy(buffer, old.length, tmp, old.length, buffer.length);
			old = tmp;
		}

		buffers.put( c, old );

	}

	private String formatConnection( IConnection c ){
		return c.getAddress().getHostAddress() +":"+c.getPort();
	}

}
