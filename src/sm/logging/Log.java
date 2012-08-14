package sm.logging;

import java.io.IOException;

import sm.logging.ILogger.LogLevel;
import sm.logging.implementation.JoinedLogger;
import sm.logging.implementation.LocalTextLogger;
import sm.logging.implementation.StandardErrorLogger;

public class Log {

	{
		ILogger stdError = new StandardErrorLogger(-1);
		ILogger text = null;
		try {
			text = new LocalTextLogger("", LogLevel.Fatal.getFlag());
		} catch (IOException e) {
			text = null;
		}
		
		if( text == null ){
			logInstance = stdError; 
		}else{
			logInstance = new JoinedLogger( stdError, text );
		}
		
	}
	
	private static ILogger logInstance = null;
	
	public static void setLogger( ILogger log ){
		logInstance = log;
	}
	
	public static void e( String format, Object ... obj ){
		String str = String.format(format, obj );
		e( str );
	}
	public static void e( String msg ){
		logInstance.Log(msg, ILogger.LogLevel.Error);
	}
	public static void e( String msg, Throwable t ){
		logInstance.Log(msg, t, LogLevel.Error);
	}
	
	public static void v( String format, Object ... obj ){
		String str = String.format( format, obj );
		v( str );
	}
	public static void v( String msg ){
		logInstance.Log(msg, LogLevel.Verbose);
	}
	public static void v( String msg, Throwable t ){
		logInstance.Log(msg, t, LogLevel.Verbose);
	}
	
	public static void d( String format, Object ... obj ){
		String str = String.format( format, obj );
		v( str );
	}
	public static void d( String msg ){
		logInstance.Log(msg, LogLevel.Debug);
	}
	public static void d( String msg, Throwable t ){
		logInstance.Log(msg, t, LogLevel.Debug);
	}
	
	public static void i( String format, Object ... obj ){
		String str = String.format( format, obj );
		v( str );
	}
	public static void i( String msg ){
		logInstance.Log(msg, LogLevel.Info);
	}
	public static void i( String msg, Throwable t ){
		logInstance.Log(msg, t, LogLevel.Info);
	}
	
	public static void w( String format, Object ... obj ){
		String str = String.format( format, obj );
		v( str );
	}
	public static void w( String msg ){
		logInstance.Log(msg, LogLevel.Warn);
	}
	public static void w( String msg, Throwable t ){
		logInstance.Log(msg, t, LogLevel.Warn);
	}
	
	public static void f( String format, Object ... obj ){
		String str = String.format( format, obj );
		v( str );
	}
	public static void f( String msg ){
		logInstance.Log(msg, LogLevel.Fatal);
	}
	public static void f( String msg, Throwable t ){
		logInstance.Log(msg, t, LogLevel.Fatal);
	}
}
