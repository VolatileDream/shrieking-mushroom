package core.uservars.implementation;

import core.uservars.IVariable;
import core.uservars.IVariableStore;

public class DefaultVariableStore implements IVariableStore {

	private IVariableStore store = new UserVariableStore();
	
	public DefaultVariableStore(){
		this("");
	}
	
	public DefaultVariableStore( String rootNamespace ){
		Encryption( rootNamespace );
		Networking( rootNamespace );
		Logging( rootNamespace );
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
		return store.TryGetVariable( name, array );
	}

	private void Logging( String rootNameSpace ){
		String loggingNameSpace = rootNameSpace +"logging.";
		
		IVariable var = null;
		
		var = new UserVariable( loggingNameSpace + "logProfile","31");
		this.AddOrChangeValue( var );
	}
	
	private void Encryption( String rootNameSpace ){
		String encryptionNameSpace = rootNameSpace+"encryption.";
		Symmetric( encryptionNameSpace );
		Asymmetric( encryptionNameSpace );
	}
	
	private void Asymmetric( String encryptionNameSpace ){
		
		String asymNameSpace = encryptionNameSpace +"asymmetric.";
		
		IVariable var = null;
		
		var = new UserVariable( asymNameSpace + "algorithm","RSA");
		this.AddOrChangeValue( var );
		
		var = new UserVariable( asymNameSpace + "key_size","2048");//bits
		this.AddOrChangeValue( var );
	}
	
	private void Symmetric( String encryptionNameSpace ){
		
		String symNameSpace = encryptionNameSpace + "symmetric.";
		
		IVariable var = null;
		
		var = new UserVariable(symNameSpace + "algorithm","TWOFISH");
		this.AddOrChangeValue( var );
		
		var = new UserVariable( symNameSpace + "key_size","256");//bits
		this.AddOrChangeValue( var );
		
		var = new UserVariable( symNameSpace + "padding","PKCS");//TODO 
		this.AddOrChangeValue( var );
		
		
	}
	
	private void Networking( String rootNameSpace ){
		String networkingNameSpace = rootNameSpace + "networking.";
		
		Unicast( networkingNameSpace );
		Multicast( networkingNameSpace );
		Messages( networkingNameSpace );
	}

	private void Messages( String networkingNameSpace ){
		
		String messageNameSpace = networkingNameSpace + "messages.";
		
		IVariable var = null;
		
		var = new UserVariable( messageNameSpace + "treat_as_tortured","false");
		this.AddOrChangeValue( var );
	}
	
	private void Unicast( String networkingNameSpace ){
		String unicastNameSpace = networkingNameSpace + "unicast.";
		
		IVariable var = null;
		
		var = new UserVariable( unicastNameSpace + "buffer_size", "4096");
		this.AddOrChangeValue( var );
	}
	
	private void Multicast( String networkingNameSpace ){
		String multicastNameSpace = networkingNameSpace + "multicast.";
		
		IVariable var = null;
		
		var = new UserVariable( multicastNameSpace + "ttl", "4");
		this.AddOrChangeValue( var );
		
		var = new UserVariable( multicastNameSpace + "packet_size", "1024");
		this.AddOrChangeValue( var );
		
		var = new UserVariable( multicastNameSpace + "so_timeout", "10000");
		this.AddOrChangeValue( var );
	}
	
}
