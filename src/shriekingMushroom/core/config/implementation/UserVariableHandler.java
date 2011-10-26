package shriekingMushroom.core.config.implementation;

import shriekingMushroom.core.config.IVariable;
import shriekingMushroom.core.config.IVariableHandler;
import shriekingMushroom.core.config.IVariableStore;
import shriekingMushroom.core.config.exceptions.VariableError;
import shriekingMushroom.core.config.exceptions.VariableNotBoolException;
import shriekingMushroom.core.config.exceptions.VariableNotIntException;
import shriekingMushroom.core.config.implementation.def.DefaultNetworkingVariableStore;

public class UserVariableHandler implements IVariableHandler {

	@Override
	public IVariable GetRequiredVariable(String name, IVariableStore store) {
		IVariable[] v_pt = new IVariable[1];

		if (store.TryGetVariable(name, v_pt)) {
			return v_pt[0];
		}
		return HandleMissingRequiredVariable(name, store);
	}

	@Override
	public int GetRequiredVariableAsInt(String name, IVariableStore store) {

		int[] i_pt = new int[1];
		IVariable var = GetRequiredVariable(name, store);

		if (var.TryGetValue(i_pt)) {
			return i_pt[0];
		}

		var = HandleMissingRequiredVariable(name, store);
		if (var.TryGetValue(i_pt)) {
			return i_pt[0];
		}

		throwRequiredVariableMissingError(name, new VariableNotIntException(
				name));
		return -1;// Execution will not reach here
	}

	@Override
	public boolean GetRequiredVariableAsBoolean(String name,
			IVariableStore store) {

		boolean[] b_pt = new boolean[1];
		IVariable var = GetRequiredVariable(name, store);

		if (var.TryGetValue(b_pt)) {
			return b_pt[0];
		}

		var = HandleMissingRequiredVariable(name, store);
		if (var.TryGetValue(b_pt)) {
			return b_pt[0];
		}

		throwRequiredVariableMissingError(name, new VariableNotBoolException(
				name));
		return false;// execution won't get here
	}

	@Override
	public IVariable HandleMissingRequiredVariable(String name,
			IVariableStore store) {

		IVariableStore newStore = new DefaultNetworkingVariableStore();
		IVariable[] var = new IVariable[1];

		if (newStore.TryGetVariable(name, var)) {
			store.AddOrChangeValue(var[0]);
			return var[0];
		}
		throwRequiredVariableMissingError(name, null);
		return null;// execution won't reach here
	}

	private void throwRequiredVariableMissingError(String name,
			Exception byProduct) throws VariableError {
		String message = getRequiredVariableMissingMessage(name);
		Error er = null;
		if (byProduct != null) {
			er = new VariableError(message, byProduct);
		} else {
			er = new VariableError(message);
		}
		throw er;
	}

	private String getRequiredVariableMissingMessage(String name) {
		return "Variable : '"
				+ name
				+ "' couldn't be found in the existing or default variable stores";
	}
}
