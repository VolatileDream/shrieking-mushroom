package networking.implementation;

import java.io.IOException;
import java.util.ArrayList;

import networking.IConnection;
import networking.events.IErrorEvent;
import networking.events.INetworkEvent;
import networking.events.IReadEvent;
import networking.implementation.events.ErrorEvent;
import networking.implementation.events.ReadEvent;

import core.CommonAccessObject;
import core.events.IEventQueue;
import core.logging.ILogger.LogLevel;

public class ConnectionThread implements Runnable {

	private final CommonAccessObject cao;
	private final IEventQueue<INetworkEvent> eventQueue;
	private final ArrayList<InternalConnection> connections = new ArrayList<InternalConnection>();

	private volatile Boolean keepRunning = true;

	public ConnectionThread( CommonAccessObject c, IEventQueue<INetworkEvent> eq ){
		cao = c;
		eventQueue = eq;
	}

	@Override
	public void run(){

		while( keepRunning ){

			ArrayList<InternalConnection> removals = new ArrayList<InternalConnection>();

			//go through all the connections
			for( InternalConnection con : connections ){

				if( con.isClosed() ){
					removals.add( con );
					continue;
				}

				try {

					handleConnection( con );

				} catch (IOException e) {

					removals.add( con );

					cao.log.Log( e, LogLevel.Error );

					IErrorEvent er = new ErrorEvent( con );

					if( !eventQueue.offer( er ) ){
						cao.log.Log( "Couldn't add error event to queue", LogLevel.Error );
					}

					try {
						con.close();
					} catch (IOException e1) {}

				}// end error handling

			}//end loop through all connections

			synchronized( connections ){
				for( InternalConnection con : removals ){
					connections.remove( con );	
				}
			}

			try {
				Thread.sleep( 100 );
			} catch (InterruptedException e) {
				cao.log.Log( e, LogLevel.Warn );
			}

		}//end running loop

		//TODO cleanup connections

	}

	/**
	 * Handles the connection
	 * @param con the connection to handle
	 * @throws IOException
	 */
	private void handleConnection( InternalConnection con ) throws IOException {

		if( con.numSend() > 0 ){
			con.flush();
		}// end send check

		byte[] read = con.read();
		if( read != null && read.length > 0 ){
			IReadEvent evt = new ReadEvent( con, read );
			if( ! eventQueue.offer( evt ) ){
				cao.log.Log( "Couldn't add read event to the queue", LogLevel.Error );
			}
		}
	}

	public void close(){
		synchronized( keepRunning ){
			keepRunning = false;
		}
	}

	public boolean addConnection( InternalConnection c ){
		synchronized( connections ){
			if( connections.size() >= maxConnectionPerThread() ){
				return false;
			}else{
				connections.add( c );
				return true;
			}
		}
	}

	public int numConnections(){
		return connections.size();
	}

	public ArrayList<IConnection> getConnections(){
		ArrayList<IConnection> result = new ArrayList<IConnection>();
		result.addAll( connections );
		return result;
	}

	private int maxConnectionPerThread(){
		String var = "networking.unicast.max_thread_connections";
		return cao.handler.GetRequiredVariableAsInt( var, cao.store );
	}

}
