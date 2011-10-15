package core.common;

import core.config.IVariableHandler;
import core.config.IVariableStore;
import core.logging.ILogger;

public class CommonAccessObject {
	
	public final IVariableStore store;
	public final IVariableHandler handler;
	public final ILogger log;
	
	public CommonAccessObject( IVariableStore st, IVariableHandler h, ILogger l){
		store = st;
		handler = h;
		log = l;
	}
	
}
