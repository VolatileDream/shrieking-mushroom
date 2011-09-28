package networking.exceptions;

import java.io.IOException;

public class MulticastUnavailableException extends IOException {

	public MulticastUnavailableException() {}

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
