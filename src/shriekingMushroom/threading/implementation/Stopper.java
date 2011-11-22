package shriekingMushroom.threading.implementation;

import shriekingMushroom.threading.IResetableStopper;

public class Stopper implements IResetableStopper {

	private Boolean running = true;

	@Override
	public synchronized boolean hasStopped() {
		return !running;
	}

	@Override
	public synchronized void reset() {
		running = true;
	}

	@Override
	public synchronized void setStop() {
		running = false;
	}

}