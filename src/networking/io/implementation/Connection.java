package networking.io.implementation;

import java.util.LinkedList;
import java.util.Queue;

import networking.io.IMessage;
import networking.io.IMessageFactory;
import networking.io.IMessageStream;
import core.multithreading.ITimeMarker;
import core.multithreading.implementation.TimeMark;
import core.uservars.IVariableHandler;
import core.uservars.IVariableStore;

public abstract class Connection implements IMessageStream {

	private final Queue<IMessage> messageQueue;
	
	protected ITimeMarker lastReceive = new TimeMark();
	protected ITimeMarker lastSent = new TimeMark();
	
	protected final IMessageFactory msgFactory;
	protected final IVariableStore varStore;
	protected final IVariableHandler varHandler;

	protected ConnectionStatus status = ConnectionStatus.Closed; 

	protected Connection( IVariableStore store, IMessageFactory fac, IVariableHandler h ){
		messageQueue = new LinkedList<IMessage>();
		msgFactory = fac;
		varStore = store;
		varHandler = h;
	}

	@Override
	public long lastSent(){
		return lastSent.getMark();
	}
	
	@Override
	public long lastReceived(){
		return lastReceive.getMark();
	}
	
	public ConnectionStatus getStatus(){
		return status;
	}

	public boolean isClosed(){
		return status == ConnectionStatus.Closed;
	}
	
	protected IMessage[] getAndClearMessages() {
		IMessage[] tmp = null;
		
		synchronized( messageQueue ){
			
			int queueSize = messageQueue.size();
			
			if( queueSize > 0 ){
				tmp = new IMessage[queueSize];
			}
			
			int i=0;
			while( !messageQueue.isEmpty() ){
				tmp[i] = messageQueue.remove();
				i++;
			}
		}
		
		return tmp;
	}

	@Override
	public int numSend() {
		int num = 0;
		
		synchronized( messageQueue ){
			num = messageQueue.size();
		}
		
		return num;
	}

	@Override
	public boolean addMessage( IMessage m) {

		if( status == ConnectionStatus.Closed ){
			return false;
		}
		boolean added = false;

		synchronized( messageQueue ){
			
			added = messageQueue.offer( m );
			
		}
		return added;
	}

}
