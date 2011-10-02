package core.config.implementation.def;

import core.config.IVariable;
import core.config.IVariableStore;
import core.config.implementation.UserVariableStore;

public class DefaultProtocolVariableStore implements IVariableStore {

	private IVariableStore store = new UserVariableStore();
	
	public DefaultProtocolVariableStore(){}
	
	@Override
	public void AddOrChangeValue(IVariable var) {
		store.AddOrChangeValue( var );
	}

	@Override
	public IVariable[] GetVariables() {
		return store.GetVariables();
	}

	@Override
	public boolean TryGetVariable(String name, IVariable[] array) {
		return store.TryGetVariable(name, array);
	}

}
