package core.threading;

public interface IStopper {
	
	/**
	 * Checks to see if the IStopper has been stopped.
	 * @return Returns true if this IStopper has stopped.
	 */
	public boolean hasStopped();
	
	/**
	 * Puts the IStopper into a stopped state.
	 */
	public void setStop();
	
	/**
	 * Resets the IStopper state to running.
	 */
	public void reset();
	
}
