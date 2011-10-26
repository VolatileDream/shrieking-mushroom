package shriekingMushroom.core;

import shriekingMushroom.core.config.IVariableHandler;
import shriekingMushroom.core.config.IVariableStore;
import shriekingMushroom.core.logging.ILogger;

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
