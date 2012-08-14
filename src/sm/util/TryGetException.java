package sm.util;

public class TryGetException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6688329203633496849L;

	public TryGetException() {
	}

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
