package shriekingmushroom.threading.implementation;

import shriekingmushroom.threading.IStopper;
/**
 * AND on the IStoppers passed in -> all of them must stop before this signals a stop
 */
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
