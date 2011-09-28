package networking.io.implementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;

import networking.exceptions.ConnectionClosedException;
import networking.exceptions.MulticastUnavailableException;
import networking.io.INetworkIO;

public class MulticastIO implements INetworkIO {

	private final MulticastSocket socket;
	private final InetAddress inet;
	private final InetSocketAddress sockAddress;
	private final NetworkInterface netAddress;
	private final int port;
	private final int bufferSz;
	
	/**
	 * Creates a MulticastIO object, which will setup the socket passed to it.
	 * @param soc
	 * @param ip
	 * @param port
	 * @param bufSz
	 */
	public MulticastIO( NetworkInterface nif, InetAddress ip, int port, int bufSz, int ttl, int soTimeOut ) throws IOException {
		
		inet = ip;
		this.port = port;
		bufferSz = bufSz;
		netAddress = nif;
		sockAddress = new InetSocketAddress( ip, port );
		
		socket = new MulticastSocket( port );
		
		socket.setTimeToLive( ttl );
		socket.setSoTimeout( soTimeOut );
		socket.joinGroup( sockAddress, nif);
	}
	
	@Override
	public void send( byte[] b ) throws IOException {
		
		requireOpenConnection();
		
		boolean error = false;
		DatagramPacket packet = new DatagramPacket( b, b.length, inet, port );
		
		try{
			send( packet );
			
		}catch( IOException e ){
			
			error = true;
			throw new IOException("Can't write to multicast", e);
			
		}finally{
			if( error && !socket.isClosed() ){
				socket.close();
			}
		}
	}
	
	private void send( DatagramPacket packet ) throws IOException,MulticastUnavailableException {
		try {
			socket.send(packet);
		} catch (Exception e) {
			
			String s = e.getMessage();
			if(s.equals("Network is unreachable") || s.equals("No such device") ){
				//can't send at all. Won't even work with localhost
				throw new MulticastUnavailableException("Can't send datagrams at all", e );
			}
			e.printStackTrace();
		}
	}
	
	@Override
	public byte[] read() throws ConnectionClosedException, IOException {
		
		requireOpenConnection();
		
		boolean error = false;
		byte[] result = null;
		byte[] buf = new byte[this.bufferSz];
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length, inet, port);
		
		try {
			socket.receive(packet);
			int len = packet.getLength();
			if( len > 0 ){
				result = new byte[len];
				System.arraycopy(packet.getData(), 0, result, 0, len);
			}
		} catch (SocketTimeoutException e1){
			// swallow, we just return null.
		} catch (IOException e) {
			
			error = true;
			throw new IOException("Couldn't recieve from multicast",e);
			
		}finally{
			
			if( error && !socket.isClosed() ){
				socket.close();
			}
		}
		return result;
	}
	
	@Override
	public void close(){
		// TODO proper close procedure?
		try {
			synchronized( this ){
				socket.leaveGroup( sockAddress, netAddress );
			}
		} catch ( IOException e) {
			System.err.println("Exception while attempting to leave multicast group on interface " + netAddress.getDisplayName());
			e.printStackTrace();
		}

		socket.disconnect();
		socket.close();
	}
	
	private void requireOpenConnection() throws ConnectionClosedException {
		if( socket.isClosed() ){
			throw new ConnectionClosedException("Connection is closed");
		}
	}
	
	@Override
	public INetworkIO strip(){
		return null;
	}
	
	/**
	 * This function waits to receive a multicast packet that starts with 'wait', and returns the contents, throws SocketTimeoutException if a packet was not received in the time allowed
	 * @param time Time to wait to time out, will keep
	 * @param sock Multicast socket to listen on
	 * @param start String to wait for
	 * @param Length length of the buffer to use
	 * @return returns the contents of a packet that starts with 'wait'
	 * @throws SocketTimeoutException If no packet was recieved in the time allowed 
	 */
	private static byte[] getPacketWithStart(int time, MulticastSocket sock, String start,int length) throws SocketTimeoutException{
		
		if( length < start.length() ){
			throw new IllegalArgumentException("Buffer length must be at least start string length");
			
		}
		//socket receive time out has already been set, the time out we've been passed
		// is meant for the return of this function
		String msg = "";
		long start_time = System.currentTimeMillis();
		while( System.currentTimeMillis() - start_time < time){
			try {
				byte[] contents = new byte[length];
				if( contents == null ){
					throw new IOException();
				}
				//DatagramPacket p = new DatagramPacket
				//byte[] contents = getPacketContents(sock, length);
				msg = new String( contents );
				if( msg.startsWith( start ) ){
					return contents;
				}
			}catch(SocketTimeoutException e){
				
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			
		}
		throw new SocketTimeoutException("Timed out, didn't get a message starting with: " + start);
	}
	
}
