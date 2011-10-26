package shriekingMushroom.core.logging.implementation;

import shriekingMushroom.core.logging.ILogger;
import shriekingMushroom.core.logging.LoggingFailedException;

public class JoinedLogger implements ILogger {

	private final ILogger[] loggers;

	public JoinedLogger(ILogger... l) {
		loggers = l;
	}

	@Override
	public void Log(String str, LogLevel l) throws LoggingFailedException {
		for (ILogger log : loggers) {
			log.Log(str, l);
		}
	}

	@Override
	public void Log(Exception e, LogLevel l) throws LoggingFailedException {
		for (ILogger log : loggers) {
			log.Log(e, l);
		}
	}

	@Override
	public void Log(String str, Exception e, LogLevel l)
			throws LoggingFailedException {
		for (ILogger log : loggers) {
			log.Log(str, e, l);
		}
	}

}
