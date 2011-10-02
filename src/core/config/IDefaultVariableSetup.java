package core.config;

public interface IDefaultVariableSetup {
	
	/**
	 * This method takes in a IVariableStore and attempts to query
	 * specific variables that are required for the running of the program.
	 * If it can't find the ones required, it adds them as their default
	 * values.
	 * @param store The IVariableStore to add default variables to
	 */
	public void Setup( IVariableStore store );
	
}
