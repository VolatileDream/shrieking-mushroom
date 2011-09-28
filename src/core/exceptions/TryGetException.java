package core.exceptions;

public class TryGetException extends IllegalArgumentException {

	public TryGetException() {}

	public TryGetException(String s) {
		super(s);
	}

	public TryGetException(Throwable cause) {
		super(cause);
	}

	public TryGetException(String message, Throwable cause) {
		super(message, cause);
	}

}
