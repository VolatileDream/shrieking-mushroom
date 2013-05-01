package com.quantum.shriek;

import java.io.IOException;
import java.util.concurrent.Executor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.quantum.shriek.events.Event;
import com.quantum.shriek.events.EventBuilder;
import com.quantum.shriek.tcp.TcpMushroom;
import com.quantum.shriek.threading.Stopable;

public class ShriekingMushroom implements Stopable {
	
	private Disruptor<Event> disrupt;
	private EventBuilder builder;
	private TcpMushroom tcp;
	
	@SafeVarargs
	public ShriekingMushroom( int bufferSize, Executor exec, EventHandler<Event> ... events ){	
		disrupt = new Disruptor<Event>( EventBuilder.FACTORY, bufferSize, exec);
		disrupt.handleEventsWith( events );
		RingBuffer<Event> buffer = disrupt.start();
		builder = new EventBuilder( buffer );
	}
	
	public TcpMushroom getTcp() throws IOException {
		synchronized (this) {
			if( tcp == null ){
				tcp = new TcpMushroom(builder);
			}
		}
		return tcp;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		tcp.stop();
		
		// events shouldn't hit the disruptor any more
		disrupt.shutdown();
	}

}
