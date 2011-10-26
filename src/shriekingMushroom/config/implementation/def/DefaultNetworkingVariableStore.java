package shriekingMushroom.config.implementation.def;

import shriekingMushroom.config.IVariable;
import shriekingMushroom.config.IVariableStore;
import shriekingMushroom.config.implementation.UserVariable;
import shriekingMushroom.config.implementation.UserVariableStore;

public class DefaultNetworkingVariableStore implements IVariableStore {

	private IVariableStore store = new UserVariableStore();

	public DefaultNetworkingVariableStore() {
		this("");
	}

	public DefaultNetworkingVariableStore(String rootNamespace) {
		Threading(rootNamespace);

		Networking(rootNamespace);
		Logging(rootNamespace);
	}

	@Override
	public void AddOrChangeValue(IVariable var) {
		store.AddOrChangeValue(var);
	}

	@Override
	public IVariable[] GetVariables() {
		return store.GetVariables();
	}

	@Override
	public boolean TryGetVariable(String name, IVariable[] array) {
		return store.TryGetVariable(name, array);
	}

	// ------------------------ Variables ------------------------

	private void Threading(String rootNamespace) {
		String threadingNamespace = rootNamespace + "threading.";

		IVariable var = null;

		var = new UserVariable(threadingNamespace + "default_sleep_millis",
				"100");// default time to sleep in threads
		this.AddOrChangeValue(var);
	}

	private void Logging(String rootNameSpace) {
		String loggingNameSpace = rootNameSpace + "logging.";

		IVariable var = null;

		var = new UserVariable(loggingNameSpace + "logFile", "res/logFile.txt");
		this.AddOrChangeValue(var);

		var = new UserVariable(loggingNameSpace + "logProfile", "31");
		this.AddOrChangeValue(var);
	}

	private void Networking(String rootNameSpace) {
		String networkingNameSpace = rootNameSpace + "networking.";

		Unicast(networkingNameSpace);
		Multicast(networkingNameSpace);
	}

	private void Unicast(String networkingNameSpace) {
		String unicastNameSpace = networkingNameSpace + "unicast.";

		IVariable var = null;

		var = new UserVariable(unicastNameSpace + "listen_port", "54444");
		this.AddOrChangeValue(var);

		var = new UserVariable(unicastNameSpace + "server_wait_timeout", "2000");
		this.AddOrChangeValue(var);

		var = new UserVariable(unicastNameSpace + "max_thread_connections",
				"10000");
		this.AddOrChangeValue(var);

	}

	private void Multicast(String networkingNameSpace) {
		String multicastNameSpace = networkingNameSpace + "multicast.";

		IVariable var = null;

		var = new UserVariable(multicastNameSpace + "ttl", "4");
		this.AddOrChangeValue(var);

		var = new UserVariable(multicastNameSpace + "packet_size", "1024");
		this.AddOrChangeValue(var);

		var = new UserVariable(multicastNameSpace + "so_timeout", "10000");
		this.AddOrChangeValue(var);
	}

}
