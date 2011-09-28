package core.uservars.exceptions;

public abstract class VariableNotExpectedTypeException extends VariableException {

	public VariableNotExpectedTypeException( String name, String message) {
		super( name, message);
	}

	public VariableNotExpectedTypeException(String name, String message, Throwable cause) {
		super( name, message, cause);
	}

	protected static String getMessageString( String name, String type ){
		return "Variable : '" + name + "' could not be parsed to type : " + type + "";
	}
	
}
