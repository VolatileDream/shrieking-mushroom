package shriekingMushroom.core.config.exceptions;

public abstract class VariableNotExpectedTypeException extends
		VariableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5199942880103768949L;

	public VariableNotExpectedTypeException(String name, String message) {
		super(name, message);
	}

	public VariableNotExpectedTypeException(String name, String message,
			Throwable cause) {
		super(name, message, cause);
	}

	protected static String getMessageString(String name, String type) {
		return "Variable : '" + name + "' could not be parsed to type : "
				+ type + "";
	}

}
