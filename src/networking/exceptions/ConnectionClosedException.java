package networking.exceptions;

import java.io.IOException;

public class ConnectionClosedException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5190247894072433735L;

	public ConnectionClosedException() {}

	public ConnectionClosedException(String message) {
		super(message);
	}

	public ConnectionClosedException(Throwable cause) {
		super(cause);
	}

	public ConnectionClosedException(String message, Throwable cause) {
		super(message, cause);
	}

}
