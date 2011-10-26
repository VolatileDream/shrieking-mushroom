package shriekingMushroom.core.config.exceptions;

public class VariableNotIntException extends VariableNotExpectedTypeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1332234417075849718L;

	public VariableNotIntException(String message) {
		super(message, VariableNotExpectedTypeException.getMessageString(
				message, "int"));
	}

	public VariableNotIntException(String message, Throwable cause) {
		super(message, VariableNotExpectedTypeException.getMessageString(
				message, "int"), cause);
	}

}
