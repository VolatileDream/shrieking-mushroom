package shriekingMushroom.config.implementation.def;

import shriekingMushroom.config.IVariable;
import shriekingMushroom.config.IVariableStore;

public class EnglishLanguageStore implements IVariableStore {

	private IVariableStore store;

	public EnglishLanguageStore() {
		this("language");
	}

	public EnglishLanguageStore(String root) {

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
