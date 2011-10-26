package core.threading;

public interface IResetableStopper extends IStopableStopper {

	/**
	 * Resets the IStopper state to running.
	 */
	public void reset();

}
