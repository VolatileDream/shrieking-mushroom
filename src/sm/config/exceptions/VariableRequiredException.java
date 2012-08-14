package sm.config.exceptions;

public class VariableRequiredException extends VariableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8878771073321486266L;

	public VariableRequiredException(String variableName) {
		super(variableName, getMessageString(variableName));
	}

	public VariableRequiredException(String variableName, Throwable cause) {
		super(variableName, getMessageString(variableName), cause);
	}

}
