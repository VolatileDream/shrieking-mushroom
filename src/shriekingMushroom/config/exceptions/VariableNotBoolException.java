package shriekingMushroom.config.exceptions;

public class VariableNotBoolException extends VariableNotExpectedTypeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3825684602369285666L;

	public VariableNotBoolException(String name) {
		super(name, VariableNotExpectedTypeException.getMessageString(name,
				"boolean"));
	}

	public VariableNotBoolException(String name, Throwable cause) {
		super(name, VariableNotExpectedTypeException.getMessageString(name,
				"boolean"), cause);
	}

}
