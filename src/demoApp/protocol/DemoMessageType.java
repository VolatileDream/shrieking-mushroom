package demoApp.protocol;

import java.util.Locale;

public class DemoMessageType {

	public final String typeName;

	public DemoMessageType(String name) {
		// set the local so that string comparisons work okay.
		typeName = name.toUpperCase(Locale.CANADA);
	}

}
