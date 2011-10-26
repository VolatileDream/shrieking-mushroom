package shriekingMushroom.networking.implementation.tcp;

import java.io.IOException;
import java.net.InetAddress;

import shriekingMushroom.core.CommonAccessObject;
import shriekingMushroom.core.logging.ILogger.LogLevel;
import shriekingMushroom.networking.implementation.ConnectionFactory;
import shriekingMushroom.networking.implementation.interfaces.InternalConnection;


public class Client {

	private final ConnectionFactory fac;
	private final CommonAccessObject cao;
	private final InetAddress addr;
	private final int port;
	private Boolean used = false;

	public Client(ConnectionFactory f, CommonAccessObject c, InetAddress net,
			int port) {
		fac = f;
		cao = c;
		addr = net;
		this.port = port;
	}

	public InternalConnection Connect() throws IOException {

		synchronized (used) {
			if (used) {
				cao.log.Log("Calling event on the same Client more then once.",
						LogLevel.Warn);
				throw new IOException("Client already used");
			} else {
				used = true;
			}
		}

		InternalConnection con = fac.ConstructUnicastClient(addr, port);
		return con;
	}

}
