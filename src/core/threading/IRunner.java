package core.threading;

public interface IRunner {
	
	/**
	 * Starts the runner on a new thread
	 */
	public void start();
	
	/**
	 * Notifies the runner thread that it should exit once convenient
	 */
	public void stop();
	
}
