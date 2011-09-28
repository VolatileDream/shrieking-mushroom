package core.uservars.tests;

import java.io.File;
import java.io.IOException;

import core.test.BaseTest;
import core.test.ITest;
import core.test.TestUtils;
import core.test.TestingException;
import core.uservars.IVariable;
import core.uservars.IVariableFactory;
import core.uservars.IVariableStore;
import core.uservars.implementation.LocalTextVariableFactory;
import core.uservars.implementation.UserVariable;
import core.uservars.implementation.UserVariableStore;

public class UserVariableFactoryTests extends BaseTest implements ITest {

	@Override
	public boolean subTest() {
		
		IVariableFactory fac = new LocalTextVariableFactory();
		
		inOutVar( fac );
		
		saveLoadVar( fac );
		
		return true;
	}

	private static void inOutVar( IVariableFactory fac ){
		IVariable var = new UserVariable( TestUtils.getRandomString(':'), TestUtils.getRandomString(':') );
		
		String start = fac.ToSaveString( var );
		IVariable var2 = fac.FromSaveString( start );

		TestUtils.AssertSame( var.GetName(), var2.GetName() );
		TestUtils.AssertSame( var.GetValue(), var2.GetValue() );
	}
	
	private static void saveLoadVar( IVariableFactory fac ){
		IVariableStore st = new UserVariableStore();
		IVariableStore[] s_pt = new UserVariableStore[1];
		
		for( int i=0; i < 10; i++){
			st.AddOrChangeValue( TestUtils.getRandomVar() );
		}
		
		try {
			File tmp = File.createTempFile( "myTemporaryFile" ,".tmp");
			
			if( !tmp.exists() ){
				throw new TestingException("test file doesn't exist : " + tmp.getAbsolutePath() );
			}
			
			if( !fac.SaveVariablesToLocation( tmp.getAbsolutePath(), st ) ){
				throw new TestingException("Couldn't save variables to file");
			}
			
			if( fac.TryGetVariablesFromLocation( tmp.getAbsolutePath(), s_pt ) ){
				UserVariableStoreTests.ensureSame(st.GetVariables(), s_pt[0].GetVariables());
			}else{
				throw new TestingException("Couldn't load variables from time file.");
			}
			
			if( !tmp.delete() ){
				throw new TestingException("Couldn't delete the test file : " + tmp.getAbsolutePath() );
			}
			
		} catch (IOException e) {
			throw new TestingException(e.getMessage());
		}
		
	}
	
	
}
