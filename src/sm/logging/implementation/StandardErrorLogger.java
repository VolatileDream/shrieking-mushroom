package sm.logging.implementation;

import sm.logging.ILogger;
import sm.logging.LoggingFailedException;

public class StandardErrorLogger extends BaseLogger implements ILogger {

	public StandardErrorLogger(int flags) {
		super(flags);
	}

	@Override
	protected void log(String s) throws LoggingFailedException {
		System.err.println(s);
	}

}
