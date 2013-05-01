import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import com.lmax.disruptor.EventHandler;
import com.quantum.shriek.ShriekingMushroom;
import com.quantum.shriek.events.Event;
import com.quantum.shriek.tcp.TcpConnection;
import com.quantum.shriek.tcp.TcpMushroom;


public class TestRun {

	public static void main( String[] args ) throws IOException, InterruptedException, URISyntaxException {
		
		Executor execPool = Executors.newFixedThreadPool( 4 );
		int port = 8080;
		InetAddress localhost = Inet6Address.getByName("localhost");
		
		
		ShriekingMushroom server = new ShriekingMushroom(512, execPool, buildHandler("Hello") );
		TcpMushroom tcpServer = server.getTcp();
		AutoCloseable acServer = tcpServer.listen( port );

		ShriekingMushroom client = new ShriekingMushroom(512, execPool, buildHandler("Other") );
		TcpMushroom tcpClient = client.getTcp();
		
		tcpClient.connect( localhost, port );
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
					TcpConnection conn = event.getConnection();
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
