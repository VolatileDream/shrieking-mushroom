package core.logging;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class LocalTextLogger implements ILogger {

	private FileOutputStream out = null;
	private final int matchFlags;
	
	public LocalTextLogger( String logFile, int logFlags ) throws IOException {
		matchFlags = logFlags;
		Exception err = null;
		try {
			//open in append mode
			out = new FileOutputStream( logFile, true );
		} catch ( FileNotFoundException e ){
			err = e;
		} catch (SecurityException e ) {
			err = e;
		}

		if( err != null ){
			throw new IOException("Couldn't create logger", err);
		}
		
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
	public void Log(String str, Exception e, LogLevel l) throws LoggingFailedException {
		handleLogging(str, e, l);
	}

	private void handleLogging( String str, Exception e, LogLevel l ) throws LoggingFailedException {
		if( (l.getFlag() & matchFlags) > 0 ){
			String s = getLogString(str, e, l) + '\n';
			try {
				out.write( s.getBytes() );
			} catch (IOException e1) {
				throw new LoggingFailedException( e1 );
			}
		}
	}
	
	private String getLogString( String str, Exception e, LogLevel l ){
		Date d = new Date();
		String result = d.toString() +" ~ "+ l.name() + ":" + str;
		if( e != null ){
			result += "@\n";
			StackTraceElement[] elms = e.getStackTrace();
			for( StackTraceElement st : elms ){
				result += "\t" + st.toString() + "\n";
			}
		}
		return result;
	}
	
}
