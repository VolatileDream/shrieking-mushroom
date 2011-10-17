package core.threading;

public interface IWaiter {

	/**
	 * Waits for a predefined amount of time 
	 * @throws InterruptedException If the current thread was interrupted while waiting.
	 */
	public void doWait() throws InterruptedException;
	
}
