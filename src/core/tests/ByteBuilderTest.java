package core.tests;

import core.ByteBuilder;
import core.Util;
import core.test.BaseTest;
import core.test.ITest;
import core.test.TestUtils;

public class ByteBuilderTest extends BaseTest implements ITest {

	@Override
	public boolean subTest() {
		ByteBuilder bb = new ByteBuilder();
		
		int length = 10;
		
		byte[] first = Util.getRandom( length );
		byte[] second = Util.getRandom( length );
		byte[] third = Util.getRandom( length );
		
		bb.append( first );
		bb.append( second );
		bb.append( third );
		
		byte[] out = bb.getBytes();
		
		for( int i=0; i < length; i++ ){
			assertSame( first, out, i, i );
		}
		for( int i=0; i < length; i++ ){
			assertSame( second, out, i, length+i );
		}
		for( int i=0; i < length; i++ ){
			assertSame( third, out, i, 2*length+i );
		}
		TestUtils.AssertSame( out.length, length*3 );
		
		return true;
	}

	private void assertSame( byte[] a, byte[] b, int pos, int pos2 ){
		byte first = a[pos];
		byte second = b[pos2];
		TestUtils.AssertSame(first, second);
	}
	
}
