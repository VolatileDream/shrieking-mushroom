package core.logging.implementation;

import core.logging.ILogger;
import core.logging.LoggingFailedException;

public class StandardErrorLogger extends BaseLogger implements ILogger {

	public StandardErrorLogger(int flags) {
		super(flags);
	}

	@Override
	protected void log(String s) throws LoggingFailedException {
		System.err.println(s);
	}

}
