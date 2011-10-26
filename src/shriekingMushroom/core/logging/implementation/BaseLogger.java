package shriekingMushroom.core.logging.implementation;

import java.util.Date;

import shriekingMushroom.core.logging.ILogger;
import shriekingMushroom.core.logging.LoggingFailedException;


public abstract class BaseLogger implements ILogger {

	private final int matchFlags;

	protected BaseLogger(int f) {
		matchFlags = f;
	}

	@Override
	public void Log(String str, LogLevel l) throws LoggingFailedException {
		handleLogging(str, null, l);
	}

	@Override
	public void Log(Exception e, LogLevel l) throws LoggingFailedException {
		handleLogging(e.getMessage(), e, l);
	}

	@Override
	public void Log(String str, Exception e, LogLevel l)
			throws LoggingFailedException {
		handleLogging(str, e, l);
	}

	protected abstract void log(String s) throws LoggingFailedException;

	protected void handleLogging(String str, Exception e, LogLevel l)
			throws LoggingFailedException {
		if ((l.getFlag() & matchFlags) > 0) {
			String s = getLogString(str, e, l) + '\n';
			log(s);
		}
	}

	private String getLogString(String str, Exception e, LogLevel l) {
		Date d = new Date();
		String result = d.toString() + " ~ " + l.name() + ":" + str;
		if (e != null) {
			result += "@\n";
			StackTraceElement[] elms = e.getStackTrace();
			for (StackTraceElement st : elms) {
				result += "\t" + st.toString() + "\n";
			}
		}
		return result;
	}

}
