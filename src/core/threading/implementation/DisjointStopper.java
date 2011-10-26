package core.threading.implementation;

import core.threading.IStopper;

public class DisjointStopper implements IStopper {

	private final IStopper[] stoppers;

	public DisjointStopper(IStopper... s) {
		stoppers = s;
	}

	@Override
	public synchronized boolean hasStopped() {
		boolean running = true;
		for (IStopper s : stoppers) {
			if (s.hasStopped()) {
				running = false;
				break;
			}
		}
		return !running;
	}

}
