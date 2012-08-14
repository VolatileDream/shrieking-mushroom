package sm;

import sm.config.IVariableHandler;
import sm.config.IVariableStore;
import sm.logging.ILogger;

public class CommonAccessObject {

	public final IVariableStore store;
	public final IVariableHandler handler;
	public final ILogger log;

	public CommonAccessObject(IVariableStore st, IVariableHandler h, ILogger l) {
		store = st;
		handler = h;
		log = l;
	}

}
