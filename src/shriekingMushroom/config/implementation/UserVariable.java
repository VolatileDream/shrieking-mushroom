package shriekingMushroom.config.implementation;

import shriekingMushroom.config.IVariable;
import shriekingMushroom.util.Util;

public class UserVariable implements IVariable {

	private final String name;
	private String value;

	public UserVariable(String name, String val) {
		this.name = name;
		this.value = val;
	}

	public String GetName() {
		return name;
	}

	@Override
	public boolean TryGetValue(int[] array) {

		Util.TryGetArrayCheck(array);

		try {
			array[0] = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean TryGetValue(boolean[] array) {

		Util.TryGetArrayCheck(array);

		if (value.equalsIgnoreCase("true")) {
			array[0] = true;
		} else if (value.equalsIgnoreCase("false")) {
			array[0] = false;
		} else {
			return false;
		}

		return true;
	}

	@Override
	public String GetValue() {
		return value;
	}

	@Override
	public void SetValue(String o) {
		value = o;
	}

}