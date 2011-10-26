package shriekingMushroom;

import shriekingMushroom.config.IVariableHandler;
import shriekingMushroom.config.IVariableStore;
import shriekingMushroom.logging.ILogger;

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
