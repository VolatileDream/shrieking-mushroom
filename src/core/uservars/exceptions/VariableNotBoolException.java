package core.uservars.exceptions;

public class VariableNotBoolException extends VariableNotExpectedTypeException {

	public VariableNotBoolException(String name ) {
		super(name, VariableNotExpectedTypeException.getMessageString(name, "boolean") );
	}

	public VariableNotBoolException(String name, Throwable cause) {
		super(name, VariableNotExpectedTypeException.getMessageString(name, "boolean"), cause);
	}

}
