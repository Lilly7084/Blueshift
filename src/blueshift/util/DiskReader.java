package blueshift.util;

import java.awt.image.BufferedImage;

public class DiskReader {
	
	private int[] pixels;
	private int width;
	private int height;
	
	private int wpos = 0;
	private int xpos = 0;
	private int ypos = 0;
	private int zpos = 0;
	
	public byte[] data;
	
	public DiskReader(BufferedImage image) {
		
		width = image.getWidth();
		height = image.getHeight();
		pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		
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
		
		int index = xpos + ypos * width;
		int rgb = pixels[index];
		int shift = zpos + 16 - (wpos * 8);
		int bit = (rgb >> shift) & 1;
		
		wpos++;
		if (wpos < 3) return bit;
		wpos = 0;
		
		xpos++;
		if (xpos < width) return bit;
		xpos = 0;
		
		ypos++;
		if (ypos < height) return bit;
		ypos = 0;
		
		zpos++;
		if (zpos < 4) return bit;
		zpos = 0;
		
		System.err.println("Warning: End of disk image has been reached!");
		return bit;
		
	}

}
