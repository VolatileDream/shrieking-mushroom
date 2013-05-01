package com.quantum.shriek.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.quantum.shriek.threading.ChannelThread;
import com.quantum.shriek.threading.IStopper;


public class TcpThread extends ChannelThread implements Runnable {

	private static final Logger logger = LogManager.getLogger( TcpThread.class );
	
	private TcpMushroom mushroom;
	
	TcpThread( TcpMushroom m, Selector s, IStopper stop ){
		super( s, stop );
		this.mushroom = m;
	}
		
	protected void accept( SelectionKey key ) throws IOException {
		
		ServerSocketChannel serv = (ServerSocketChannel) key.channel();
		
		logger.debug("Accepting Socket connection: {}", serv.getLocalAddress() );
		
		SocketChannel chan = serv.accept();
		
		logger.debug("Accepted Socket connection from {}", chan.getRemoteAddress() );
		
		if( chan != null ){
			TcpConnection tcp = mushroom.createTcpConnection(chan);
			
			mushroom.eventBuilder().connectionCreated( tcp );
		}
	}
	
	protected void connect( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = (TcpConnection) key.attachment();
		
		logger.debug("Finising connection to {}", chan.getRemoteAddress() );
		
		if( chan.finishConnect() ){
			mushroom.eventBuilder().connectionCreated( tcp );
		}
	}
	
	protected void write( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = (TcpConnection) key.attachment();
		
		ByteBuffer buf = null;
		
		// find something to write
		while( tcp.hasWrite() ){
			buf = tcp.fetchWrite();
			if( buf != null && buf.hasRemaining() ){
				break;
			}
		}
		
		if( buf != null ){
					
			if( ! buf.isReadOnly() ){
				logger.debug("Writing to {}. Data {}", chan.getRemoteAddress(), buf.array() );
			}else{
				logger.debug("Writing to {}", chan.getRemoteAddress() );
			}
			
			chan.write( buf );
		}
	}

	protected void read( SelectionKey key ) throws IOException {
		
		SocketChannel chan = (SocketChannel) key.channel();
		TcpConnection tcp = (TcpConnection) key.attachment();
		
		logger.debug("Reading from {}", chan.getRemoteAddress() );
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.clear();
		
		int read = chan.read(buf);
		
		if( read <= 0 ){
			return;
		}
		
		buf.flip();

		logger.trace("Reading from {}. Data {}", chan.getRemoteAddress(), buf.array() );
		
		mushroom.eventBuilder().readCompleted( tcp, buf );
	}
	
}
