package orb.quantum.shriek;

import java.io.IOException;
import java.util.concurrent.Executor;

import orb.quantum.shriek.events.Event;
import orb.quantum.shriek.events.EventBuilder;
import orb.quantum.shriek.tcp.TcpMushroom;
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
	private TcpMushroom tcp;
	private UdpMushroom udp;
	
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
				logger.debug("TCP Created");
				tcp = new TcpMushroom(builder);
			}
		}
		return tcp;
	}

	public UdpMushroom getUdp() throws IOException {
		synchronized (this){
			if( udp == null ){
				logger.debug("UDP Created");
				udp = new UdpMushroom(builder);
			}
		}
		return udp;
	}
	
	@Override
	public void stop() {
		
		// close all, tcp, udp, multicast
		
		synchronized (this) {
			if( tcp != null ){
				tcp.stop();
			}
			
			if ( udp != null ){
				udp.stop();
			}
			
			// events shouldn't hit the disruptor any more
			disrupt.shutdown();
		}		
	}

}
