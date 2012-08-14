package shriekingmushroom.threading.implementation;

import shriekingmushroom.threading.IStopper;

/**
 * OR on IStopper passed in -> only one needs to stop for this to signal a stop
 */
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
