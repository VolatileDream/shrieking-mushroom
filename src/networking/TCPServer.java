package networking;

public interface TCPServer {
	
	/**
	 * Starts the tcp server
	 */
	public void start();
	
	/**
	 * Stops the tcp server.
	 */
	public void stop();
	
	/**
	 * Reopens the port that the tcp server was listening on.
	 * <br>Will error if stop hasn't been called.
	 * <br>It will wait for the TCP server to stop from the previous stop call before restarting.
	 */
	public void restart();
	
}
