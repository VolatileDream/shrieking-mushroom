package protocol.implementation;

import java.util.Locale;

public class MessageType {

	public final String typeName;
	
	public MessageType( String name ){
		//set the local so that string comparisons work okay.
		typeName = name.toUpperCase( Locale.CANADA );
	}
	
}
