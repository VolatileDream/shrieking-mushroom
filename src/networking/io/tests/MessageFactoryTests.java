package networking.io.tests;

import networking.exceptions.InsufficientMessageLengthException;
import networking.exceptions.MalformedMessageException;
import networking.io.IMessage;
import networking.io.IMessageFactory;
import networking.io.MessageType;
import networking.io.implementation.Message;
import networking.io.implementation.MessageFactory;
import core.Tupple;
import core.test.BaseTest;
import core.test.TestUtils;
import core.test.TestingException;
import core.tests.UtilTests;

public class MessageFactoryTests extends BaseTest {

	@Override
	public boolean subTest() {
		
		int length = (int)(Math.random()*200);
		
		MessageType t = new MessageType("Text");
		
		IMessageFactory fac = new MessageFactory(null, t);
		IMessage m = new Message(
				core.test.TestUtils.getRandomString( length ),
				t
			);
		
		byte[] b = fac.toBytes( m );
		Tupple<IMessage, Integer> tup;
		try {
			tup = fac.fromBytes(b, false);
		} catch (Exception e) {
			throw new RuntimeException( e );
		}
		
		if( tup.Item2 != b.length ){
			throw new TestingException("Lengths don't match up ( "+ tup.Item2 +","+ b.length +" )");
		}
		
		byte[] msg = m.getContents();
		byte[] msg2 = tup.Item1.getContents();
		
		boolean same = msg.length == msg2.length;
		
		if( !same ){
			throw new TestingException("Lengths of message contents don't match up ("+msg2.length+","+msg.length+")");
		}
		
		for( int i=0; i < msg.length; i++){
			TestUtils.AssertSame( msg[i], msg[i] );
		}
		
		if( !same ){
			throw new TestingException( "Messages aren't the same" );
		}
		
		return true;
	}

}
