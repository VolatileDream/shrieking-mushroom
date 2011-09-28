package core.uservars.tests;

import core.test.BaseTest;
import core.test.ITest;
import core.test.TestUtils;
import core.test.TestingException;
import core.uservars.IVariable;
import core.uservars.IVariableStore;
import core.uservars.implementation.UserVariable;
import core.uservars.implementation.UserVariableStore;

public class UserVariableStoreTests extends BaseTest implements ITest {

	@Override
	public boolean subTest() {
		
		int maxValues = 5;
		int length = 12;
		
		IVariableStore store = new UserVariableStore();
		
		String[] names = new String[maxValues];
		String[] values = new String[maxValues];
		String[] newValues = new String[maxValues];
		
		setupValues( names, length );
		setupValues( values, length );
		setupValues( newValues, length );
		
		createVariables( names, values, store );
		
		ensureSame( names, values, store  );
				
		IVariable[] storedVars = store.GetVariables();
		TestUtils.AssertSame( storedVars.length, maxValues );
		
		ensureSame( names, values, storedVars );
		
		//change variables
		
		createVariables( names, newValues, store );
		
		ensureSame( names, newValues, store  );
		
		storedVars = store.GetVariables();
		TestUtils.AssertSame( storedVars.length, maxValues );
		
		ensureSame( names, newValues, storedVars );
		
		return true;
	}

	private static void setupValues( String[] str, int length ){
		for( int i=0; i < str.length; i++){
			str[i] = TestUtils.getRandomString();
		}
	}
	
	private static void createVariables( String[] names, String[] values, IVariableStore store ){
		for( int i=0; i < names.length ; i++ ){	
			IVariable var = new UserVariable( names[i], values[i] );
			store.AddOrChangeValue( var );
		}
	}
	
	private static void ensureSame( String[] names, String[] values, IVariableStore store ){
		IVariable[] v_pt = new IVariable[1];
		v_pt[0] = null;
		for( int i=0; i < names.length; i++){
			
			if( !store.TryGetVariable( names[i], v_pt) ){
				throw new TestingException("Couldn't retrieve variable '"+ names[i] +"' after adding it");
			}
			
			TestUtils.AssertSame( v_pt[0].GetValue(), values[i] );
			
		}
	}

	static void ensureSame( IVariable[] vars1, IVariable[] vars2 ){
		for( int i=0; i < vars1.length; i++){
			
			String oName = vars1[i].GetName();
			String oValue = vars1[i].GetValue();
			boolean set = false;
			
			for( int j=0; j < vars2.length; j++){
				
				String varName = vars2[j].GetName();
				
				if( oName.equals( varName ) ){
					
					String varValue = vars2[j].GetValue();
					
					if( !oValue.equals( varValue ) ){
						throw new TestingException(
								"Variable '"+
								oName +
								"' value ("+
								varValue+
								") was not the same as input value : '"+
								oValue+
								"'.");
					}else{
						set = true;
						break;
					}
				}
			}// end inner loop
			
			if( !set ){
				throw new TestingException("Couldn't find variable '"+ oName +"' in the list");
			}
		}
	}

	private static void ensureSame( String[] name, String[] values, IVariable[] vars){
		IVariable[] vars2 = new IVariable[name.length];
		for( int i=0; i < name.length; i++){
			vars2[i] = new UserVariable(name[i],values[i]);
		}
		ensureSame( vars2, vars );
	}
}
