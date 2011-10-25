package core.config.implementation.def;

import core.config.IVariable;
import core.config.IVariableStore;
import core.config.implementation.UserVariable;
import core.config.implementation.UserVariableStore;

public class DefaultProtocolVariableStore implements IVariableStore {

	private IVariableStore store = new UserVariableStore();
	
	public DefaultProtocolVariableStore(){
		this("");
	}
	
	public DefaultProtocolVariableStore( String str ){
		queue(str);
	}
	
	@Override
	public void AddOrChangeValue(IVariable var) {
		store.AddOrChangeValue( var );
	}

	@Override
	public IVariable[] GetVariables() {
		return store.GetVariables();
	}

	@Override
	public boolean TryGetVariable(String name, IVariable[] array) {
		return store.TryGetVariable(name, array);
	}

	//----------------------------------- Variable setup -----------------------------------
	
	private void queue(String root){
		String queueNameSpace = root + "queue.";
		
		IVariable var = null;
		
		var = new UserVariable( queueNameSpace + "poll_time_milli", "200");
		this.AddOrChangeValue( var );
		
	}
	
}
