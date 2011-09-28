package core.uservars.tests;

import core.Util;
import core.test.BaseTest;
import core.test.ITest;
import core.test.TestingException;
import core.uservars.IVariable;
import core.uservars.IVariableHandler;
import core.uservars.IVariableStore;
import core.uservars.exceptions.VariableError;
import core.uservars.implementation.UserVariable;
import core.uservars.implementation.UserVariableHandler;

public class UserVariableHandlerTests extends BaseTest implements ITest {

	/*
	IVariable GetRequiredVariable( String name, IVariableStore store );
	int GetRequiredVariableAsInt( String name, IVariableStore store );
	boolean GetRequiredVariableAsBoolean( String name, IVariableStore store );
	IVariable HandleMissingRequiredVariable( String name, IVariableStore store );
	*/
	
	
	@Override
	public boolean subTest() {
		
		IVariableHandler vH = new UserVariableHandler();
		StoreStub store = new StoreStub();
		
		boolean error = false;
		
		try {
			correctIntCast( vH, store );
		} catch (VariableError e1) {
			throw new TestingException("Cast to int should have been valid");
		}
		try {
			correctBoolCast(vH, store );
		} catch (VariableError e1) {
			throw new TestingException("Cast to boolean should have been valid");
		}
		try {
			variableExists(vH, store);
		} catch (VariableError e1) {
			throw new TestingException("The variable should exist");
		}
		
		error = false;
		try {
			incorrectBoolCast(vH, store);
		} catch (VariableError e) {
			error = true;
		}
		if( ! error ){
			throw new TestingException("The variable shouldn't have been cast to bool");
		}
		
		
		error = false;
		try {
			incorrectIntCast(vH, store);
		} catch (VariableError e) {
			error = true;
		}
		if( ! error ){
			throw new TestingException("The variable shouldn't have been cast to int");
		}
		
		
		error = false;
		try {
			nonexistantVar(vH, store);
		} catch (VariableError e) {
			error = true;
		}
		if( ! error ){
			throw new TestingException("The variable shouldn't exist");
		}
		
		return true;
	}

	private void correctIntCast( IVariableHandler vH, StoreStub store ){
		vH.GetRequiredVariableAsInt("vInt", store);
	}
	
	private void correctBoolCast( IVariableHandler vH, StoreStub store ){
		vH.GetRequiredVariableAsBoolean("vBool", store);
	}
	
	private void variableExists( IVariableHandler vH, StoreStub store ){
		vH.GetRequiredVariable("vStr", store);
	}
	
	private void incorrectIntCast( IVariableHandler vH, StoreStub store ){
		vH.GetRequiredVariableAsInt("vBool", store );
	}
	
	private void incorrectBoolCast( IVariableHandler vH, StoreStub store ){
		vH.GetRequiredVariableAsBoolean("vStr", store );
	}
	
	private void nonexistantVar( IVariableHandler vH, StoreStub store ){
		vH.GetRequiredVariableAsInt("v_noexists", store );
	}
	
	class StoreStub implements IVariableStore {

		IVariable var1 = new UserVariable("vInt", "2");
		IVariable var2 = new UserVariable("vBool", "false");
		IVariable var3 = new UserVariable("vStr", "a");
		
		@Override
		public void AddOrChangeValue(IVariable var) {
			var3 = var;
		}

		@Override
		public IVariable[] GetVariables() {
			IVariable[] vars = new IVariable[3];
			vars[0] = var1;
			vars[1] = var2;
			vars[2] = var3;
			return vars;
		}

		@Override
		public boolean TryGetVariable(String name, IVariable[] array) {
			Util.TryGetArrayCheck( array );
			if( name.equals( var1.GetName() ) ){
				array[0] = var1;
			}else if( name.equals( var2.GetName() ) ){
				array[0] = var2;
			}else{
				array[0] = var3;
			}
			return array[0] != null;
		}
		
	}
	
	
}
