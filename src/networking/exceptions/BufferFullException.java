package networking.exceptions;

import java.io.IOException;

public class BufferFullException extends IOException {

	public BufferFullException(String message) {
		super(message);
	}

	public BufferFullException(Throwable cause) {
		super(cause);
	}

	public BufferFullException(String message, Throwable cause) {
		super(message, cause);
	}

}
