package core.uservars.exceptions;

public class VariableRequiredException extends VariableException {

	public VariableRequiredException(String variableName) {
		super( variableName, getMessageString( variableName ) );
	}

	public VariableRequiredException(String variableName, Throwable cause) {
		super( variableName, getMessageString( variableName ), cause);
	}

}
