import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import orb.quantum.shriek.ShriekingMushroom;
import orb.quantum.shriek.common.Connection;
import orb.quantum.shriek.events.Event;
import orb.quantum.shriek.tcp.TcpMushroom;

import com.lmax.disruptor.EventHandler;


public class TestRun {
	
	public static void main( String[] args ) throws IOException, InterruptedException, URISyntaxException {
		
		Logger l = LogManager.getLogger(TestRun.class);
		
		l.error("abc");
		
		ExecutorService execPool = Executors.newFixedThreadPool( 4 );
		int port = 8080;
		InetAddress localhost = Inet6Address.getByName("localhost");
		
		System.out.println("Address + Executor");
		
		ShriekingMushroom server = new ShriekingMushroom(512, execPool, buildHandler("Server") );
		TcpMushroom tcpServer = server.getTcp();
		
		long start = System.currentTimeMillis();
		System.out.println("TCP Server Start: " + start);
		AutoCloseable acServer = tcpServer.listen( port );

		long end = System.currentTimeMillis();
		System.out.println("TCP Server End: " + end);
		System.out.println("Diff: " + (end-start) );
		
		ShriekingMushroom client = new ShriekingMushroom(512, execPool, buildHandler("Client") );
		
		start = System.currentTimeMillis();
		System.out.println("TCP Client Start: " + start);
		
		TcpMushroom tcpClient = client.getTcp();
		
		tcpClient.connect( localhost, port );
		
		end = System.currentTimeMillis();
		System.out.println("TCP Client End: " + end);
		System.out.println("Diff: " + (end-start) );
		
		Thread.sleep(1000);
		
		System.out.println("Shutting down");
		
		try {
			acServer.close();
		} catch (Exception e) { /* totally ignored */ }
		
		server.stop();
		client.stop();
		
		Thread.sleep(1000);
		
		System.out.println("Terminating thread pools...");
		
		execPool.shutdown();
	}

	private static Charset charset = Charset.defaultCharset();

	private static EventHandler<Event> buildHandler( final String name ){
		return new EventHandler<Event>() {
			
			private int eventCount = 0;
			
			private void printHello( Event e, CharSequence str ){
				System.out.println( name + " ( " +e.getTimestamp()+ ", " + e.getEventType().toString() + " ):" + str);
			}
			@Override
			public void onEvent(Event event, long sequence, boolean endOfBatch) throws Exception {
				if( event.getData() == null ){
					printHello(event, "");
				}else{
					CharBuffer cb = charset.decode( event.getData() );
					printHello(event, cb);
				}
				
				// just go back and forth for a bit.
				if ( eventCount < 5 ){
					Connection conn = event.getConnection();
					if( ! conn.write( buffer(name) ) ){
						System.err.println("Write did not succeed");
					}
				}

				eventCount ++;
			}
		};
	}

	public static ByteBuffer buffer( String text ){

		ByteBuffer buf = ByteBuffer.allocate( text.length() );

		buf.put( text.getBytes() );

		buf.flip();

		return buf;
	}


}
