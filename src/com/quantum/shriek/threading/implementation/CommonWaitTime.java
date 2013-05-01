package com.quantum.shriek.threading.implementation;

import com.quantum.shriek.threading.IWaiter;

public class CommonWaitTime implements IWaiter {

	private final long time;

	public CommonWaitTime(long t) {
		time = t;
	}

	@Override
	public void doWait() throws InterruptedException {
		Thread.sleep(time);
	}

}
