package shriekingmushroom.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import shriekingmushroom.events.net.ConnectEvent;
import shriekingmushroom.events.net.ReadEvent;
import shriekingmushroom.threading.ChannelThread;
import shriekingmushroom.threading.IStopper;

public class TcpThread extends ChannelThread implements Runnable {

	private TcpMushroom mushroom;
	
	TcpThread( TcpMushroom m, Selector s, IStopper stop ){
		super( s, stop );
		this.mushroom = m;
	}
		
	protected void accept( SelectionKey key ) throws IOException {
		
		ServerSocketChannel serv = (ServerSocketChannel) key.channel();
		
		SocketChannel chan = serv.accept();
		
		if( chan != null ){
			TcpConnection tcp = mushroom.createTcpConnection(chan);
			
			mushroom.eventQueue().add( new ConnectEvent( tcp ) );
		}
		
	}
	
	protected void connect( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = (TcpConnection) key.attachment();
		
		if( chan.finishConnect() ){
			mushroom.eventQueue().add( new ConnectEvent( tcp ) );
		}
		
	}
	
	protected void write( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = (TcpConnection) key.attachment();
		
		ByteBuffer buf = null;
		
		// find something to write
		while( tcp.hasWrite() ){
			buf = tcp.fetchWrite();
			if( ! buf.hasRemaining() ){
				continue;
			}
		}
		
		if( buf != null ){
			chan.write( buf );
		}
		
	}

	protected void read( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = (TcpConnection) key.attachment();
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.clear();
		
		int read = chan.read(buf);
		
		if( read <= 0 ){
			return;
		}
		
		buf.flip();

		mushroom.eventQueue().add( new ReadEvent( tcp, buf ) );
		
	}
	
}
