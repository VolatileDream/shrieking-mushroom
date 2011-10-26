package shriekingMushroom.logging.implementation;

import shriekingMushroom.logging.ILogger;
import shriekingMushroom.logging.LoggingFailedException;

public class StandardErrorLogger extends BaseLogger implements ILogger {

	public StandardErrorLogger(int flags) {
		super(flags);
	}

	@Override
	protected void log(String s) throws LoggingFailedException {
		System.err.println(s);
	}

}
