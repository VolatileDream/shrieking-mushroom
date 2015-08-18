package orb.quantum.shriek.common;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class RemovableKey implements AutoCloseable {

	private SelectionKey key;
	
	public RemovableKey( SelectionKey key ){
		this.key = key;
	}
	
	@Override
	public void close() throws IOException {
		// close the connection
		key.channel().close();
		// cancel the key
		key.cancel();
	}

}
