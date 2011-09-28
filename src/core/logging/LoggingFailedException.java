package core.logging;

public class LoggingFailedException extends RuntimeException {

	public LoggingFailedException() {}

	public LoggingFailedException(String message) {
		super(message);
	}

	public LoggingFailedException(Throwable cause) {
		super(cause);
	}

	public LoggingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
