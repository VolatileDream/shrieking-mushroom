package core.uservars.exceptions;

public class VariableError extends Error {

	public VariableError(String message) {
		super(message);
	}

	public VariableError(String message, Throwable cause) {
		super(message, cause);
	}

}
