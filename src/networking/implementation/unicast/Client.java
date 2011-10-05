package networking.implementation.unicast;

import java.io.IOException;
import java.net.InetAddress;

import networking.implementation.ConnectionFactory;
import networking.implementation.interfaces.InternalConnection;

import core.CommonAccessObject;
import core.logging.ILogger.LogLevel;

public class Client {
	
	private final ConnectionFactory fac;
	private final CommonAccessObject cao;
	private final InetAddress addr;
	private final int port;
	private Boolean used = false;
	
	public Client( ConnectionFactory f, CommonAccessObject c, InetAddress net, int port ){
		fac = f;
		cao = c;
		addr = net;
		this.port = port;
	}
	
	public InternalConnection Connect() throws IOException {
		
		synchronized( used ){
			if( used ){
				cao.log.Log("Calling event on the same Client more then once.", LogLevel.Warn );
				throw new IOException("Client already used");
			}else{
				used = true;
			}
		}
		
		InternalConnection con = fac.ConstructUnicastClient( addr, port );
		return con;		
	}
	
}
