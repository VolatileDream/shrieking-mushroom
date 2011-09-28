package core.uservars.exceptions;

public class VariableNotIntException extends VariableNotExpectedTypeException {

	public VariableNotIntException(String message) {
		super( message, VariableNotExpectedTypeException.getMessageString( message, "int") );
	}

	public VariableNotIntException(String message, Throwable cause) {
		super( message, VariableNotExpectedTypeException.getMessageString( message, "int") , cause);
	}

}
