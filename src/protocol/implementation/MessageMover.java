package protocol.implementation;

import protocol.IMessageFilter;
import protocol.ITranslator;

public class MessageMover implements Runnable {
	
	private final Object threadLock = new Object();
	private final IMessageFilter<MyMessage> filter;
	private final ITranslator<MyMessage> translator;
	
	private Boolean keepRunning = false;
	private Thread thread;
	
	
	public MessageMover( IMessageFilter<MyMessage> f, ITranslator<MyMessage> tr ){
		filter = f;
		translator = tr;
	}
	
	@Override
	public void run(){
		while( keepRunning ){
			
			MyMessage msg = translator.getMessage();
			filter.doAction( msg );
			
		}
	}

	public void start(){
		synchronized( threadLock ){
			if( thread != null ){
				throw new RuntimeException("Can't start a thread more then once.");
			}
			thread = new Thread( this );
			thread.start();
		}
	}
	
	public void stop(){
		synchronized( threadLock ){
			keepRunning = false;
		}
	}
	
}
