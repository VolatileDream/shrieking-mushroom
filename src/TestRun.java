import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import orb.quantum.shriek.ShriekingMushroom;
import orb.quantum.shriek.common.Connection;
import orb.quantum.shriek.events.Event;
import orb.quantum.shriek.tcp.TcpMushroom;

import com.lmax.disruptor.EventHandler;


public class TestRun {

	public static void main( String[] args ) throws IOException, InterruptedException, URISyntaxException {
		
		Executor execPool = Executors.newFixedThreadPool( 4 );
		int port = 8080;
		InetAddress localhost = Inet6Address.getByName("localhost");
		
		System.out.println("Address + Executor");
		
		ShriekingMushroom server = new ShriekingMushroom(512, execPool, buildHandler("Hello") );
		TcpMushroom tcpServer = server.getTcp();
		
		long start = System.currentTimeMillis();
		System.out.println("TCP Server Start: " + start);
		AutoCloseable acServer = tcpServer.listen( port );

		long end = System.currentTimeMillis();
		System.out.println("TCP Server End: " + end);
		System.out.println("Diff: " + (end-start) );
		
		ShriekingMushroom client = new ShriekingMushroom(512, execPool, buildHandler("Other") );
		
		start = System.currentTimeMillis();
		System.out.println("TCP Client Start: " + start);
		
		TcpMushroom tcpClient = client.getTcp();
		
		tcpClient.connect( localhost, port );
		
		end = System.currentTimeMillis();
		System.out.println("TCP Client End: " + end);
		System.out.println("Diff: " + (end-start) );
		
	}

	private static Charset charset = Charset.defaultCharset();

	private static EventHandler<Event> buildHandler( final String name ){
		return new EventHandler<Event>() {
			private void printHello( Event e, CharSequence str ){
				System.out.println( name + " (" +e.getTimestamp()+ "):" + str);
			}
			@Override
			public void onEvent(Event event, long sequence, boolean endOfBatch) throws Exception {
				if( event.getData() == null ){
					printHello(event, "connected");
					Connection conn = event.getConnection();
					if( ! conn.write( buffer(name) ) ){
						System.err.println("Write did not succeed");
					}
				}else{
					CharBuffer cb = charset.decode( event.getData() );
					printHello(event, cb);
				}
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
