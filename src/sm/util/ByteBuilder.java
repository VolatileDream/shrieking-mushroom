package sm.util;

import java.util.LinkedList;
import java.util.List;

public class ByteBuilder {

	private List<byte[]> allBytes;

	public ByteBuilder() {
		allBytes = new LinkedList<byte[]>();
	}

	public void append(byte... bytes) {
		allBytes.add(bytes);
	}

	public void append(char c) {
		this.append((byte) ((c >> 2) & 0xFF),// higher bytes
				(byte) (c & 0xFF)// lower bytes
				);
	}

	public void append(String str) {
		this.append(str.getBytes());
	}

	public void appendAsInt(int i) {
		this.append((i + "").getBytes());
	}

	public byte[] getBytes() {
		int totalLength = 0;
		for (byte[] b : allBytes) {
			totalLength += b.length;
		}
		byte[] result = new byte[totalLength];

		int offset = 0;

		for (byte[] b : allBytes) {
			System.arraycopy(b, 0, result, offset, b.length);
			offset += b.length;
		}

		return result;
	}
}
