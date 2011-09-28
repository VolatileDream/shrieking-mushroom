package core.uservars.exceptions;

public class VariableException extends Exception {

	private final String varName;
	
	public VariableException( String name ) {
		varName = name;
	}

	public VariableException( String name, String message) {
		super(message);
		varName = name;
	}

	public VariableException( String name, Throwable cause) {
		super(cause);
		varName = name;
	}

	public VariableException( String name, String message, Throwable cause) {
		super(message, cause);
		varName = name;
	}
	
	protected static String getMessageString( String varName ){
		return "Variable : '" + varName + "' is a required variable, but was not found";
	}
	
	public String getVariableName(){
		return varName;
	}

}
