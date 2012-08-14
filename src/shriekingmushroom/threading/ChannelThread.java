package shriekingmushroom.threading;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public abstract class ChannelThread implements Runnable {

	private Selector select;
	private IStopper stop;
	
	public ChannelThread( Selector s, IStopper stop ){
		this.select = s;
		this.stop = stop;
	}
	
	@Override
	public final void run() {
		
		while( ! stop.hasStopped() ){
			
			try {
				doItagain();
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		//TODO deal with deregistering channels
		
	}

	private final void doItagain() throws Exception{
		
		select.select();
		
		for( SelectionKey key : select.selectedKeys() ){

			if( key.isAcceptable() ){
				accept(key);
				continue;
			}
			
			if( key.isConnectable() ){
				connect(key);
			}
			
			if( key.isWritable() ){
				write(key);
			}
		
			if( key.isReadable() ){
				read(key);
			}
			
		}
		
	}
	
	protected abstract void accept( SelectionKey key ) throws IOException ;
	
	protected abstract void connect( SelectionKey key ) throws IOException ;
	
	protected abstract void write( SelectionKey key ) throws IOException ;

	protected abstract void read( SelectionKey key ) throws IOException ;


}
