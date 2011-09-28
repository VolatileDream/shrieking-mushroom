package networking.io.tests;

import java.io.IOException;

import networking.io.IMessageFactory;
import networking.io.MessageType;
import networking.io.implementation.Message;
import networking.io.implementation.MessageFactory;
import core.logging.LocalTextLogger;
import core.logging.ILogger;
import core.test.BaseTest;
import core.test.ITest;
import core.test.TestingException;

public class MessageTests extends BaseTest implements ITest {

	@Override
	public boolean subTest(){
		boolean ret = true;
		
		MessageType[] good = getGoodTypes();
		
		IMessageFactory mfac = null;
		try {
			mfac = new MessageFactory( new LocalTextLogger("res/logfile.txt",-1), good );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for( MessageType type : good ){

			String text = getText();
			Message m = null;
			try{
				m = new Message( text, type);
				String result = new String(
						mfac.fromBytes(
								mfac.toBytes( m ),
								false
							).Item1.getContents()
					);
				if( ! text.equals( result ) ){
					ret = false;
					Failure( text, result, type );
				}

			}catch( Exception e ){
				ret = false;
				if( e != null ){
					throw new RuntimeException( e );
				}
				Failure( text, e.getMessage(), type );
			}
		}
		
		MessageType[] bad = getBadTypes();
		
		for( MessageType type : bad ){

			String text = getText();
			Message m = null;
			try{
				m = new Message( text, type);
				String result = new String(
						mfac.fromBytes(
								mfac.toBytes( m ),
								false
							).Item1.getContents()
					);
				if( text.equals( result ) ){
					ret = false;
					Failure( text, result, type );
				}
				Failure( text, result, type );
			}catch( Exception e ){
				ret = true;
			}
		}
		
		return ret;
	}

	private static MessageType[] getGoodTypes(){
		return new MessageType[]{
				new MessageType("Normal"),
				new MessageType("Text"),
				new MessageType("Spam"),
				new MessageType("???"),
				new MessageType("111"),
				new MessageType("ERH")
		};
	}
	
	private static MessageType[] getBadTypes(){
		return new MessageType[]{
				new MessageType(":Byte"),
				new MessageType("How@re you?")
		};
	}
	
	private static void Failure(String original, String different, MessageType type){
		String s = ("Failure : "+type.toString() );
		s += ("Original Text : " + original);
		s += ("Modified Text : " + different);
		throw new TestingException(s);
	}

	private static String getText(){
		return new String( core.Util.getRandom(256) );

	}

}
