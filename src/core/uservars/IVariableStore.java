package core.uservars;

public interface IVariableStore {
	
	public boolean TryGetVariable( String name, IVariable[] array );
	
	public void AddOrChangeValue( IVariable var );
	
	IVariable[] GetVariables();
	
}
