package sm.config.implementation;

import java.util.ArrayList;
import java.util.List;

import sm.config.IVariable;
import sm.config.IVariableStore;

public class JointVariableStore implements IVariableStore {

	private final IVariableStore[] stores;
	
	public JointVariableStore(IVariableStore... vars) {
		stores = vars;
	}

	@Override
	public void AddOrChangeValue(IVariable var) {
		throw new RuntimeException("Can't add a value to a joint store");
	}

	@Override
	public IVariable[] GetVariables() {
		List<IVariable> list = new ArrayList<IVariable>();
		for( IVariableStore s : stores ){
			for( IVariable v : s.GetVariables() ){
				list.add(v);
			}
		}
		return list.toArray(new IVariable[0]);
	}

	@Override
	public boolean TryGetVariable(String name, IVariable[] array) {
		for( IVariableStore st : stores ){
			boolean result = st.TryGetVariable(name, array);
			if( result ) return true;
		}
		return true;
	}

}
