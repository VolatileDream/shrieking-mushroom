package core.multithreading.implementation;

import core.multithreading.ITimeMarker;

public class TimeMark implements ITimeMarker {

	private long time;
	
	public TimeMark(){
		time = System.currentTimeMillis();
	}
	
	public synchronized void markCurrentTime(){
		time = System.currentTimeMillis();
	}
	
	public synchronized long getMark(){
		return time;
	}
	
}
