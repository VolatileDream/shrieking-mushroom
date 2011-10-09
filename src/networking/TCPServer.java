package networking;

import core.threading.IRunner;

public interface TCPServer extends IRunner {
	
	/**
	 * Reopens the port that the tcp server was listening on.
	 * <br>Will error if stop hasn't been called.
	 * <br>The call will wait for the TCP server to stop from the previous stop call before restarting.
	 */
	public void restart();
	
}
