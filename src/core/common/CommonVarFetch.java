package core.common;

import core.config.IVariableHandler;
import core.config.IVariableStore;

public class CommonVarFetch {
	
	private final IVariableStore varStore;
	private final IVariableHandler varHandler;
	
	public CommonVarFetch( IVariableStore store, IVariableHandler handler ){
		varStore = store;
		varHandler = handler;
	}
	
	public int threadingSleep(){
		String var = "threading.default_sleep_millis";
		return varHandler.GetRequiredVariableAsInt( var, varStore );
	}
	
}
