package core.threading.implementation;

import core.threading.IStopper;

public class Stopper implements IStopper {

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
