package shriekingMushroom.core.logging.implementation;

import shriekingMushroom.core.logging.ILogger;
import shriekingMushroom.core.logging.LoggingFailedException;

public class StandardErrorLogger extends BaseLogger implements ILogger {

	public StandardErrorLogger(int flags) {
		super(flags);
	}

	@Override
	protected void log(String s) throws LoggingFailedException {
		System.err.println(s);
	}

}
