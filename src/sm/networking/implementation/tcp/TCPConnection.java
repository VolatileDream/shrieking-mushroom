package sm.networking.implementation.tcp;

import java.io.IOException;
import java.net.Socket;

import sm.CommonAccessObject;
import sm.networking.implementation.Connection;
import sm.networking.implementation.interfaces.INetworkIO;


public class TCPConnection extends Connection {

	private INetworkIO ioControl;

	/**
	 * Creates a Unicast Connection with a socket.
	 * 
	 * @param s
	 *            Socket to create the connection with
	 * @param mbSize
	 *            maximum size the buffer can hold between message reads. <br>
	 *            If there are left over bytes between message reads and it
	 *            can't hold everything in the buffer it will throw a
	 *            BufferFullException
	 */
	public TCPConnection(CommonAccessObject c, Socket soc) {
		super(c, soc.getInetAddress(), soc.getPort());
		ioControl = new TCPIO(soc);
		status = ConnectionStatus.Open;
	}

	@Override
	public void close() throws IOException {
	
		synchronized (status) {
			if (status != ConnectionStatus.Open) {
				throw new IOException("Unicast Connection isn't open");
			}
			flush();
			status = ConnectionStatus.Changing;
		}

		try {
			ioControl.close();
		} catch (IOException e) {
			System.err.println("Issues closing socket to : "
					+ this.getAddress().toString() + "-" + this.getPort());
		}
		synchronized (status) {
			status = ConnectionStatus.Closed;
		}
	}

	@Override
	public void flush() throws IOException {

		synchronized (status) {
			if (status != ConnectionStatus.Open) {
				throw new IOException("Unicast Connection isn't open");
			}
		}

		byte[][] tmp = this.getAndClearMessages();

		for (byte[] m : tmp) {
			if (m != null)
				send(m);
		}

	}

	private void send(byte[] m) throws IOException {

		synchronized (status) {
			if (status != ConnectionStatus.Open) {
				throw new IOException("Unicast Connection isn't open");
			}
		}

		synchronized (ioControl) {

			ioControl.send(m);

			lastSent.markCurrentTime();
		}
	}

	@Override
	public byte[] read() throws IOException {

		byte[] nb = null;

		synchronized (ioControl) {

			nb = ioControl.read();

			if (nb != null && nb.length > 0 ) {
				lastReceive.markCurrentTime();
			}

		}
		if( nb == null ){ nb = new byte[0]; }
		return nb;
	}

}
