package networking.implementation;

import java.io.IOException;

import networking.exceptions.ConnectionClosedException;

public interface INetworkIO {
	
	/**
	 * Sends every byte in the array passed.
	 * <br>Don't pass an array with 24 bytes if you only want to send 23.
	 */
	public void send( byte[] b ) throws IOException,ConnectionClosedException ;
	
	/**
	 * Attempts to read from the source. Returns null OR byte[0] if nothing was read.
	 * It can NOT return a byte array larger then the number of bytes it has actually read.<br>
	 * ex: If you read in 23 bytes, you MUST return a byte[] of length 23.
	 * @return
	 */
	public byte[] read() throws IOException,ConnectionClosedException;
	
	public void close() throws IOException, ConnectionClosedException;
	
}
