package networking.implementation.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import networking.exceptions.ConnectionClosedException;
import networking.implementation.interfaces.INetworkIO;

public class TCPIO implements INetworkIO {

	private Socket socket;

	public TCPIO(Socket s) {
		socket = s;
	}

	@Override
	public void close() throws IOException, ConnectionClosedException {
		if (!socket.isClosed()) {
			socket.close();
		}
	}

	@Override
	public byte[] read() throws ConnectionClosedException, IOException {
		requireOpenConnection();

		boolean error = false;

		byte[] result = null;

		try {

			result = read1();

		} catch (IOException e) {
			error = true;
			throw new IOException("Can't read from socket", e);
		} finally {
			if (error && !socket.isClosed()) {
				socket.close();
			}
		}

		return result;
	}

	private byte[] read1() throws IOException, ConnectionClosedException {
		InputStream in = socket.getInputStream();

		int avail = in.available();
		byte[] buf = new byte[avail];

		int read = in.read(buf);
		if (read != avail) {
			byte[] tmp = new byte[read];
			System.arraycopy(buf, 0, tmp, 0, read);
			buf = tmp;
		}

		return buf;
	}

	@Override
	public void send(byte[] buf) throws ConnectionClosedException, IOException {
		requireOpenConnection();

		boolean error = false;

		try {

			send1(buf);

		} catch (IOException e) {

			error = true;
			throw new IOException("Can't write to socket", e);

		} finally {

			if (error && !socket.isClosed()) {
				socket.close();
			}
		}
	}

	private void send1(byte[] buf) throws ConnectionClosedException,
			IOException {
		OutputStream out = socket.getOutputStream();
		out.write(buf);
		out.flush();
	}

	private void requireOpenConnection() throws ConnectionClosedException {
		if (socket.isClosed()) {
			throw new ConnectionClosedException();
		}
	}

}
