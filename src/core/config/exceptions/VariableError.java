package core.config.exceptions;

public class VariableError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3658441907920251004L;

	public VariableError(String message) {
		super(message);
	}

	public VariableError(String message, Throwable cause) {
		super(message, cause);
	}

}
