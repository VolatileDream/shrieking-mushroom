package protocol.implementation;

import protocol.IMessageFilter;
import protocol.implementation.interfaces.MyMessage;

public class MessageFilter implements IMessageFilter<MyMessage> {

	@Override
	public void doAction( MyMessage m ) {
		System.out.println( new String( m.getContents() ) );
	}

}
