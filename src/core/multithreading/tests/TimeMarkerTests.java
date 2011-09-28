package core.multithreading.tests;

import core.multithreading.ITimeMarker;
import core.multithreading.implementation.TimeMark;
import core.test.BaseTest;
import core.test.ITest;
import core.test.TestingException;

public class TimeMarkerTests extends BaseTest implements ITest {

	private final int TIMING_THRESH_HOLD = 5;//milliseconds
	
	@Override
	public boolean subTest() {

		ITimeMarker marker = new TimeMark();
		long time = System.currentTimeMillis();
		
		checkFail( time, marker );
		
		time = System.currentTimeMillis();
		marker.markCurrentTime();
		
		checkFail( time, marker );
		
		return true;
	}

	private void checkFail( long time, ITimeMarker marker ){
		if( !within( time, marker.getMark(), TIMING_THRESH_HOLD ) ){
			Fail( time, marker.getMark(), TIMING_THRESH_HOLD );
		}
	}
	
	private boolean within( long a, long b, int c){
		return a - c < b && b < a + c;
	}
	
	private void Fail( long close, long actual, int threshhold ){
		throw new TestingException(
				"Actual time ("+actual+") not within " + threshhold +" milliseconds from expected time ("+ close +")"
			);
	}
	
}
