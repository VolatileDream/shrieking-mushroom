package com.quantum.shriek;

import java.io.IOException;
import java.util.concurrent.Executor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.quantum.shriek.events.Event;
import com.quantum.shriek.events.EventBuilder;
import com.quantum.shriek.tcp.TcpMushroom;
import com.quantum.shriek.threading.IResetableStopper;
import com.quantum.shriek.threading.implementation.Stopper;


public class ShriekingMushroom {
	
	private Disruptor<Event> disrupt;
	private EventBuilder builder;
	private IResetableStopper stopper;
	private TcpMushroom tcp;
	
	@SafeVarargs
	public ShriekingMushroom( int bufferSize, Executor exec, EventHandler<Event> ... events ){
		stopper = new Stopper();
		
		disrupt = new Disruptor<Event>( EventBuilder.FACTORY, bufferSize, exec);
		disrupt.handleEventsWith( events );
		RingBuffer<Event> buffer = disrupt.start();
		builder = new EventBuilder( buffer );
	}
	
	public TcpMushroom getTcp() throws IOException {
		synchronized (this) {
			if( tcp == null ){
				tcp = new TcpMushroom(stopper, builder);
			}
		}
		return tcp;
	}
	
	public void exit(){
		
		// this should guarantee that no more events get passed to the RingBuffer
		stopper.setStop();
		
		disrupt.shutdown();
	}

}
