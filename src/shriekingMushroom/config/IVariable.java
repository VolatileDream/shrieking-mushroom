package shriekingMushroom.config;

public interface IVariable {

	public String GetName();

	public String GetValue();

	public void SetValue(String str);

	public boolean TryGetValue(int[] i);

	public boolean TryGetValue(boolean[] i);

}
