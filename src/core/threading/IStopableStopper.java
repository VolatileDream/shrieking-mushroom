package core.threading;

public interface IStopableStopper extends IStopper {

	/**
	 * Puts the IStopper into a stopped state.
	 */
	public void setStop();

}
