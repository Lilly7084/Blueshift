package blueshift.util;

import java.awt.image.BufferedImage;

public class DiskReader {
	
	private BufferedImage image;
	private int wpos = 0;
	private int xpos = 0;
	private int ypos = 0;
	private int zpos = 0;
	
	public byte[] data;
	
	public DiskReader(BufferedImage image) {
		this.image = image;
		
		// Read header
		int payloadLength =
				(readByte()      ) |
				(readByte() <<  8) |
				(readByte() << 16) |
				(readByte() << 24);
		int payloadChecksum =
				(readByte()      ) |
				(readByte() <<  8) |
				(readByte() << 16) |
				(readByte() << 24);
		
		// Read payload
		byte[] payload = new byte[payloadLength];
		for (int i = 0; i < payloadLength; i++) {
			payload[i] = (byte) readByte();
		}
		
		// Verify payload
		int newChecksum = Toolbox.fletcher16(payload);
		if (newChecksum != payloadChecksum) {
			System.err.println("Checksum error while reading disk: Expected " + payloadChecksum + ", got " + newChecksum);
		}
		
		// Unpack payload
		data = Toolbox.inflate(payload);
		
	}
	
	private int readByte() {
		int foo = 0;
		for (int i = 0; i < 8; i++) {
			foo |= readBit() << i;
		}
		return foo;
	}
	
	private int readBit() {
		
		int rgb = image.getRGB(xpos, ypos);
		int shift = zpos + 16 - (wpos * 8);
		int bit = (rgb >> shift) & 1;
		
		wpos++;
		if (wpos < 3) return bit;
		wpos = 0;
		
		xpos++;
		if (xpos < image.getWidth()) return bit;
		xpos = 0;
		
		ypos++;
		if (ypos < image.getHeight()) return bit;
		ypos = 0;
		
		zpos++;
		if (zpos < 4) return bit;
		zpos = 0;
		
		System.err.println("Warning: End of disk image has been reached!");
		return bit;
		
	}

}
