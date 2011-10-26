package networking.exceptions;

import java.io.IOException;

public class MulticastUnavailableException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 128017867844560142L;

	public MulticastUnavailableException() {
	}

	public MulticastUnavailableException(String message) {
		super(message);
	}

	public MulticastUnavailableException(Throwable cause) {
		super(cause);
	}

	public MulticastUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

}
