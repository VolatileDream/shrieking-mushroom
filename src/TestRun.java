import java.io.IOException;
import java.net.Inet6Address;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

import shriekingmushroom.ShriekingMushroom;
import shriekingmushroom.events.Event;
import shriekingmushroom.events.net.ConnectEvent;
import shriekingmushroom.events.net.ReadEvent;
import shriekingmushroom.tcp.TcpMushroom;


public class TestRun {

	public static void main( String[] args ) throws IOException, InterruptedException {
		
		ShriekingMushroom sm = new ShriekingMushroom();
		
		TcpMushroom tcp = sm.getTcp();
		
		AutoCloseable ac = tcp.listen( 8080 );
		
		tcp.connect( Inet6Address.getByName("localhost"), 8080 );
		
		BlockingQueue<Event> events = tcp.eventQueue();
		
		Charset cs = Charset.defaultCharset();
		
		while( true ){
			
			Event e = events.poll();

			if( e == null ){
				Thread.sleep(100);
				continue;
			}
			
			System.out.print( e.getTimestamp() +" : ");
			
			if( e instanceof ReadEvent ){
				
				ReadEvent re = (ReadEvent)e;
				
				CharBuffer cb = cs.decode( re.getRead() );
				
				System.out.println( cb.toString() );
				
			}else if( e instanceof ConnectEvent ){
				
				ConnectEvent ce = (ConnectEvent)e;
				
				System.out.println( "Connected" );
				
				ce.getConnection().write( buffer() );
				
			}
			
		}
		
	}
	
	public static ByteBuffer buffer(){
		String text = "Hello World";
		
		ByteBuffer buf = ByteBuffer.allocate( text.length() );

		buf.put( text.getBytes() );
				
		buf.flip();
				
		return buf;
	}
	
	
}
