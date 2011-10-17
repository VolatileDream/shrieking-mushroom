package core.config.implementation;

import java.util.Hashtable;

import core.Util;
import core.config.IVariable;
import core.config.IVariableStore;


public class UserVariableStore implements IVariableStore {

	private Hashtable<String,IVariable> lookupTable = new Hashtable<String,IVariable>();
	
	@Override
	public void AddOrChangeValue( IVariable var ) {
			lookupTable.put( var.GetName(), var);
	}

	@Override
	public boolean TryGetVariable(String name, IVariable[] array) {
		Util.TryGetArrayCheck( array );
		array[0] = lookupTable.get( name );
		return array[0] != null;
	}
	
	@Override
	public IVariable[] GetVariables(){
		return lookupTable.values().toArray( new IVariable[0] );
	}
	
}
