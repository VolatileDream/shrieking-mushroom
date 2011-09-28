package core.test;

import core.uservars.IVariable;
import core.uservars.implementation.UserVariable;

public class TestUtils {
	
	public static <T> void AssertSame( T a, T b ){
		if( !a.equals(b) ){
			throwTestException();
		}
	}
	
	public static void AssertSame( long a, long b ){
		if( a != b ){
			throwTestException();
		}
	}
	
	public static void AssertSame( boolean a, boolean b ){
		if( a != b ){
			throwTestException();
		}
	}
	
	public static void AssertSame( char a, char b ){
		if( a != b ){
			throwTestException();
		}
	}
	
	private static void throwTestException(){
		throw new TestingException("Unequal Values");
	}

	public static String getRandomString( char without ){
		char[] chars = new char[32];
		for( int i=0; i < chars.length; i++){
			char c = getRandomChar();
			while( c == without ){
				c = getRandomChar();
			}
			chars[i] = c;
		}
		return new String( chars );
	}
	
	public static String getRandomString(){
		return getRandomString( 32 );
	}
	
	public static String getRandomString( int length ){
		
		char[] chars = new char[ length ];
		for( int i=0; i < chars.length; i++){
			char c = getRandomChar();
			chars[i] = c;
		}
		return new String( chars );
	}
	
	public static char getRandomChar(){
		return (char)( Math.random() * (0x7E - 0x21) + 0x20);
	}

	public static IVariable getRandomVar(){
		return new UserVariable( TestUtils.getRandomString(':'), TestUtils.getRandomString(':') );
	}
}
