package orb.quantum.shriek;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.Executor;

import orb.quantum.shriek.events.Event;
import orb.quantum.shriek.events.EventBuilder;
import orb.quantum.shriek.tcp.TcpMushroom;
import orb.quantum.shriek.threading.ChannelThread;
import orb.quantum.shriek.threading.Stopable;
import orb.quantum.shriek.udp.UdpMushroom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class ShriekingMushroom implements Stopable {
	
	private static final Logger logger = LogManager.getLogger( ShriekingMushroom.class );
	
	private final Disruptor<Event> disrupt;
	private final EventBuilder builder;
	
	private ChannelThread thread;
	private Selector selector;
	
	private TcpMushroom tcp;
	private UdpMushroom udp;
	
	@SafeVarargs
	public ShriekingMushroom( int bufferSize, Executor exec, EventHandler<Event> ... events ){	
		disrupt = new Disruptor<Event>( EventBuilder.FACTORY, bufferSize, exec);
		disrupt.handleEventsWith( events );
		RingBuffer<Event> buffer = disrupt.start();
		builder = new EventBuilder( buffer );
	}
	
	private ChannelThread getThread() throws IOException{
		synchronized(this){
			if( selector == null ){
				selector = Selector.open();
				if( thread == null ){
					thread = new ChannelThread( selector );
				}
			}
		}
		return thread;
	}
	
	public TcpMushroom getTcp() throws IOException {
		synchronized (this) {
			if( tcp == null ){
				tcp = new TcpMushroom(builder, getThread());
				logger.debug("TCP Created");
			}
		}
		return tcp;
	}

	public UdpMushroom getUdp() throws IOException {
		synchronized (this){
			if( udp == null ){
				logger.debug("UDP Created");
				udp = new UdpMushroom(builder, getThread());
			}
		}
		return udp;
	}
	
	@Override
	public void stop() {
		
		// close all, tcp, udp, multicast
		
		synchronized (this) {
			thread.stop();
			
			try {
				selector.close();
			} catch (IOException e) {
				logger.error(e);
			}
			
			// events shouldn't hit the disruptor any more
			disrupt.shutdown();
		}		
	}

}
