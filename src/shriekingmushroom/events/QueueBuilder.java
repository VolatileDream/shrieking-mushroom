package shriekingmushroom.events;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueBuilder {

	private int size = Integer.MAX_VALUE;
	
	public QueueBuilder(){}
	
	public QueueBuilder withSize( int s ){
		size = s;
		return this;
	}
	
	public <M> BlockingQueue<M> buildQueue(){
		return new LinkedBlockingQueue<M>( size );
	}
	
}
