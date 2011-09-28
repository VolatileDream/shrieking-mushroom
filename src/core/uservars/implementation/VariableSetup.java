package core.uservars.implementation;

import core.uservars.IDefaultVariableSetup;
import core.uservars.IVariable;
import core.uservars.IVariableStore;

public class VariableSetup implements IDefaultVariableSetup {

	private IVariableStore defaults = new DefaultVariableStore(); 
	
	
	@Override
	public void Setup( IVariableStore store ) {
		
		IVariable[] vars = defaults.GetVariables();
		
		for( IVariable v : vars ){
			
			//create our 'pointer'
			IVariable[] pt = new IVariable[1];
			
			if( !store.TryGetVariable( v.GetName() , pt ) ){
				//only add if it isn't in there.
				store.AddOrChangeValue( v );
				System.out.println(
						"Variable : '"+
						v.GetName()+
						"' wasn't created, and has been added with it's default value of : '"+
						v.GetValue()+
						"'");
			}
		}
		
	}
	
}
