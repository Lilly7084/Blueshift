package blueshift.util;

public class StreamReader {
	
	private byte[] data;
	private int index = 0;
	
	public StreamReader(byte[] data) {
		this.data = data;
	}
	
	public byte readByte() {
		return data[index++];
	}
	
	public boolean readBool() {
		byte foo = readByte();
		if (foo == 0x00) return false;
		if (foo == 0x01) return true;
		System.err.println("Malformed boolean at " + (index - 1) + ": " + Integer.toHexString(foo));
		return false;
	}
	
	public int readInt() {
		return
				(Byte.toUnsignedInt(readByte())      ) |
				(Byte.toUnsignedInt(readByte()) <<  8) |
				(Byte.toUnsignedInt(readByte()) << 16) |
				(Byte.toUnsignedInt(readByte()) << 24);
	}
	
	public String readString() {
		int length = readInt();
		byte[] data = new byte[length];
		for (int i = 0; i < length; i++) {
			data[i] = readByte();
		}
		return new String(data);
	}
	
	public int[] readTable() {
		int length = readInt();
		int[] table = new int[length];
		for (int i = 0; i < length; i++) {
			int key = readInt();
			int value = readInt();
			if (key >= length) {
				System.err.println("Table key out of range: " + key);
			}
			table[key] = value;
		}
		return table;
	}

}
