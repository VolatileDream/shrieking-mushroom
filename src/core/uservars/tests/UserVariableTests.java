package core.uservars.tests;

import core.test.BaseTest;
import core.test.ITest;
import core.test.TestUtils;
import core.test.TestingException;
import core.uservars.IVariable;
import core.uservars.implementation.UserVariable;

public class UserVariableTests extends BaseTest implements ITest {

	@Override
	public boolean subTest() {
		
		SimpleVariableAssignment();
		
		booleanCheck();
		
		intCheck();
		
		return true;
	}

	private void booleanCheck(){
		boolean[] b_pt = new boolean[1];
		IVariable isTrue = new UserVariable("vTrue", "true");
		IVariable isFalse = new UserVariable("vFalse","false");
		IVariable badCheck = new UserVariable("vBad", "a" );
		
		if( !isTrue.TryGetValue( b_pt ) || !b_pt[0] ){
			throw new TestingException("This should be parsed to true"); 
		}
		
		if( !isFalse.TryGetValue( b_pt ) || b_pt[0] ){
			throw new TestingException("This should be parsed to false");
		}
		
		if( badCheck.TryGetValue( b_pt ) ){
			throw new TestingException("This shouldn't have worked");
		}
		
	}
	
	private void intCheck(){
		int[] i_pt = new int[1];
		IVariable isOne = new UserVariable("vOne", "1");
		IVariable isTwo = new UserVariable("vTwo","2");
		IVariable isNeg = new UserVariable("vNeg", "-4");
		IVariable badCheck = new UserVariable("vBad", "a" );
		
		if( !isOne.TryGetValue( i_pt ) || i_pt[0] != 1 ){
			throw new TestingException("This should be parsed to 1"); 
		}
		
		if( !isTwo.TryGetValue( i_pt ) || i_pt[0] != 2 ){
			throw new TestingException("This should be parsed to 2");
		}
		
		if( !isNeg.TryGetValue( i_pt ) || i_pt[0] != -4 ){
			throw new TestingException("This should be parsed to -4");
		}
		
		if( badCheck.TryGetValue( i_pt ) ){
			throw new TestingException("This shouldn't have worked");
		}
		
	}
	
	
	private void SimpleVariableAssignment(){
		String name = TestUtils.getRandomString();
		String value = TestUtils.getRandomString();
		
		IVariable var = new UserVariable( name, value );
		
		if( !var.GetName().equalsIgnoreCase(name) ){
			throw new TestingException( failure( var.GetName(), name, "Name") );
		}
		if( ! var.GetValue().equalsIgnoreCase(value) ){
			throw new TestingException( failure( var.GetValue(), value, "Value") );
		}
	}
	
	private String failure( String val1, String val2, String thing ){
		return ( "Values for " + thing +" are different : '"+val1+"' and '"+val2+"'." );
	} 

}
