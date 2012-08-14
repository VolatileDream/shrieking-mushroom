package sm.networking.implementation.multicast;

import java.util.concurrent.ExecutorService;

import shriekingmushroom.threading.IRunner;
import shriekingmushroom.threading.IStopableStopper;

public class MulticastRunner implements IRunner {

	private final Runnable multicastRun;
	private final IStopableStopper stop;
	private final ExecutorService service;
	
	/**
	 * @param mr MulticastReciever to start when our start() is called
	 * @param st The stopper that the MulticastReciever is checking
	 */
	public MulticastRunner( MulticastReciever mr, IStopableStopper st, ExecutorService exec ){
		multicastRun = mr;
		stop = st;
		service = exec;
	}
	
	@Override
	public void start() {
		service.execute( multicastRun );
	}

	@Override
	public void stop() {
		stop.setStop();
	}

}
