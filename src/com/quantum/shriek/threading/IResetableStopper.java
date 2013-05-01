package com.quantum.shriek.threading;

public interface IResetableStopper extends IStopableStopper {

	/**
	 * Resets the IStopper state to running.
	 */
	public void reset();

}
