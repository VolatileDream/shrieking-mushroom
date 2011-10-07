package core.threading;

public interface IStopper {
	
	/**
	 * Checks to see if the IStopper has been stopped.
	 * @return Returns true if this IStopper has stopped.
	 */
	public boolean hasStopped();
	
}
