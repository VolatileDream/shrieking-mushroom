package shriekingmushroom.threading;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

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

		shutdownSelector();
	}

	private void shutdownSelector(){
		shutdownSelector(0);
	}
	
	private void shutdownSelector( int recursionDepth ){
		//TODO deal with deregistering channels
		
		try {
			Set<SelectionKey> keys = select.keys();

			for( SelectionKey key : keys ){
				key.channel().close();
				key.cancel();
			}
			
			select.close();
			
		} catch (ClosedSelectorException e) {

			//already closed, nothing that we can do
			// although it shouldn't be closed...

			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
			
			//try 
			shutdownSelector( recursionDepth + 1 );
		}
		
	}

	private void closeKey( SelectionKey key ){
		try {
			key.channel().close();
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		key.cancel();
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
