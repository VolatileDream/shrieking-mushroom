package com.quantum.shriek.threading.implementation;

import com.quantum.shriek.threading.ITimeMarker;

public class TimeMark implements ITimeMarker {

	private long time;

	public TimeMark() {
		time = System.currentTimeMillis();
	}

	public synchronized void markCurrentTime() {
		time = System.currentTimeMillis();
	}

	public synchronized long getMark() {
		return time;
	}

}
