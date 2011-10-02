package core.config;

public interface IVariableHandler {

	public IVariable GetRequiredVariable( String name, IVariableStore store );

	public int GetRequiredVariableAsInt( String name, IVariableStore store );
	
	public boolean GetRequiredVariableAsBoolean( String name, IVariableStore store );

	public IVariable HandleMissingRequiredVariable( String name, IVariableStore store );
	
}
