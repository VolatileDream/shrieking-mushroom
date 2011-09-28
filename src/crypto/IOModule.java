package crypto;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import networking.exceptions.ConnectionClosedException;
import networking.io.INetworkIO;

public class IOModule implements INetworkIO {

	protected INetworkIO netio;
	protected Cipher crypto;
	protected Key key;
	protected int blockSize;

	public IOModule( INetworkIO io, Cipher c, Key k, int sz ){
		netio = io;
		crypto = c;
		key = k;
		blockSize = sz;
	}

	@Override
	public void close() throws IOException, ConnectionClosedException {
		netio.close();
	}

	@Override
	public byte[] read() throws IOException, ConnectionClosedException {
		byte[] buf = netio.read();
		byte[] result = null;
		try {
			crypto.init( Cipher.DECRYPT_MODE, key);

			result = crypto.doFinal( buf );
			
			//TODO How to deal with each exception type?
			// Throw an "Oh Shit the crypto broke!" error?
			
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public void send(byte[] b) throws IOException, ConnectionClosedException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public INetworkIO strip(){
		return netio;
	}

}
