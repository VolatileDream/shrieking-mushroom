package shriekingMushroom.core.threading.implementation;

import shriekingMushroom.core.threading.IStopper;

public class ConjointStopper implements IStopper {

	private final IStopper[] stoppers;

	public ConjointStopper(IStopper... s) {
		stoppers = s;
	}

	@Override
	public synchronized boolean hasStopped() {
		boolean running = false;
		for (IStopper s : stoppers) {
			if (!s.hasStopped()) {
				running = true;
				break;
			}
		}
		return !running;
	}

}
