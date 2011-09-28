package networking.exceptions;

public class InsufficientMessageLengthException extends Exception {

	public InsufficientMessageLengthException() {}

	public InsufficientMessageLengthException(String message) {
		super(message);
	}

	public InsufficientMessageLengthException(Throwable cause) {
		super(cause);
	}

	public InsufficientMessageLengthException(String message, Throwable cause) {
		super(message, cause);
	}

}
