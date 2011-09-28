package core.tests;

import core.Util;
import core.exceptions.TryGetException;
import core.test.BaseTest;
import core.test.ITest;
import core.test.TestingException;

public class UtilTests extends BaseTest implements ITest {

	@Override
	public boolean subTest() {

		int length = 0;

		byte[] b = Util.getRandom(length);

		if( length != b.length ){
			Failure( length, b.length );
			throw new TestingException("Lengths not equal : "+ length + " != "+ b.length +" .");
		}
		
		int count = 1;
		for( int i=1; i < 10; i++ ){

			int places = Util.characterPlaces( count );
			if( places != i){
				throw new TestingException("Decimal places for '"+ count+"' actually "+ i+" not "+ places);
			}
			count *= 10;
		}
		
		count = -1;
		for( int i=1; i < 10; i++ ){
			
			int places = Util.characterPlacesAbs( count );
			if( places != i+1 ){
				throw new TestingException("Decimal places for '"+ count+"' actually "+ i+" not "+ places);
			}
			count *= 10;
		}
		
		try{
			Object[] o_pt = new Object[1];
			Util.TryGetArrayCheck( o_pt );
			
			boolean[] b_pt = new boolean[1];
			Util.TryGetArrayCheck(b_pt);
			
			int[] i_pt = new int[1];
			Util.TryGetArrayCheck(i_pt);
			
			char[] c_pt = new char[1];
			Util.TryGetArrayCheck(c_pt);
			
		}catch( TryGetException e ){
			throw new TestingException("An array didn't pass the try get test");
		}
		
		return true;
	}

	public static void Failure( int actual, int different ){
		System.err.println("Original Length : " + actual);
		System.err.println("Modified Length : " + different);
	}

}
