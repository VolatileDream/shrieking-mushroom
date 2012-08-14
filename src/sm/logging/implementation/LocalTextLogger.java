package sm.logging.implementation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sm.logging.ILogger;
import sm.logging.LoggingFailedException;


public class LocalTextLogger extends BaseLogger implements ILogger {

	private FileOutputStream out = null;

	public LocalTextLogger(String logFile, int logFlags) throws IOException {
		super(logFlags);
		Exception err = null;
		try {
			// open in append mode
			out = new FileOutputStream(logFile, true);
		} catch (FileNotFoundException e) {
			err = e;
		} catch (SecurityException e) {
			err = e;
		}

		if (err != null) {
			throw new IOException("Couldn't create logger", err);
		}
	}

	@Override
	public void log(String s) throws LoggingFailedException {
		try {
			out.write(s.getBytes());
		} catch (IOException e) {
			throw new LoggingFailedException(e);
		}
	}
}
