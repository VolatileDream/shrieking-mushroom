package core.config;

public interface IVariableFactory {
	
	String ToSaveString( IVariable var );
	
	IVariable FromSaveString( String str );
	
	public boolean TryGetVariablesFromLocation( String uri, IVariableStore[] store );
	
	public boolean SaveVariablesToLocation( String uri, IVariableStore store );
	
}
