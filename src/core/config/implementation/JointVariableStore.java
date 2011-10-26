package core.config.implementation;

import core.config.IVariable;
import core.config.IVariableStore;

public class JointVariableStore implements IVariableStore {

	private IVariableStore store = new UserVariableStore();

	public JointVariableStore(IVariableStore... vars) {
		for (IVariableStore s : vars) {
			for (IVariable v : s.GetVariables()) {
				store.AddOrChangeValue(v);
			}
		}
	}

	@Override
	public void AddOrChangeValue(IVariable var) {
		store.AddOrChangeValue(var);
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
