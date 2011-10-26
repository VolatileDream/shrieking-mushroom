package shriekingMushroom.core;

import shriekingMushroom.core.config.IVariableHandler;
import shriekingMushroom.core.config.IVariableStore;

public class CommonVarFetch {

	private final IVariableStore varStore;
	private final IVariableHandler varHandler;

	public CommonVarFetch(IVariableStore store, IVariableHandler handler) {
		varStore = store;
		varHandler = handler;
	}

	public int threadingSleep() {
		String var = "threading.default_sleep_millis";
		return varHandler.GetRequiredVariableAsInt(var, varStore);
	}

}
