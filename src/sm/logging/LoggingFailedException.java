package sm.logging;

public class LoggingFailedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -417175673179987448L;

	public LoggingFailedException() {
	}

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
