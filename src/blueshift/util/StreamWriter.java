package blueshift.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class StreamWriter {
	
	public List<Byte> data = new ArrayList<Byte>();
	
	public void writeByte(byte foo) {
		data.add(foo);
	}
	
	public void writeBool(boolean foo) {
		writeByte((byte) (foo ? 1 : 0));
	}
	
	public void writeInt(int foo) {
		writeByte((byte) ( foo        & 0xFF));
		writeByte((byte) ((foo >>  8) & 0xFF));
		writeByte((byte) ((foo >> 16) & 0xFF));
		writeByte((byte) ((foo >> 24) & 0xFF));
	}
	
	public void writeString(String foo) {
		try {
			writeInt(foo.length());
			for (byte b : foo.getBytes("UTF-8")) {
				writeByte(b);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void writeTable(int[] foo) {
		writeInt(foo.length);
		for (int i = 0; i < foo.length; i++) {
			writeInt(i);
			writeInt(foo[i]);
		}
	}
	
}
