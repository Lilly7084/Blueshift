package blueshift.util;

import java.awt.image.BufferedImage;

public class DiskWriter {
	
	private BufferedImage image;
	private int wpos = 0;
	private int xpos = 0;
	private int ypos = 0;
	private int zpos = 0;
	
	public DiskWriter(BufferedImage image) {
		this.image = image;
	}
	
	public void write(byte[] data) {
		
		// Assemble disk image
		System.out.println("Solution length : " + data.length);
		data = Toolbox.deflate(data);
		int length = data.length;
		int checksum = Toolbox.fletcher16(data);
		
		System.out.println("Payload length  : " + length);
		System.out.println("Payload checksum: " + checksum);
		
		// Write disk header
		writeByte((length        ) & 0xFF);
		writeByte((length   >>  8) & 0xFF);
		writeByte((length   >> 16) & 0xFF);
		writeByte((length   >> 24) & 0xFF);
		writeByte((checksum      ) & 0xFF);
		writeByte((checksum >>  8) & 0xFF);
		writeByte((checksum >> 16) & 0xFF);
		writeByte((checksum >> 24) & 0xFF);
		
		// Write disk payload
		for (byte foo : data) {
			writeByte(foo);
		}
		
		System.out.println("Successfully written to disk.");
		
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	private void writeByte(int foo) {
		for (int i = 0; i < 8; i++) {
			writeBit((foo >> i) & 1);
		}
	}
	
	private void writeBit(int bit) {
		
		int rgb = image.getRGB(xpos, ypos);
		int shift = zpos + 16 - wpos * 8;
		rgb = (rgb & ~(1 << shift)) | (bit << shift);
		image.setRGB(xpos, ypos, rgb);
		
		wpos++;
		if (wpos < 3) return;
		wpos = 0;
		
		xpos++;
		if (xpos < image.getWidth()) return;
		xpos = 0;
		
		ypos++;
		if (ypos < image.getHeight()) return;
		ypos = 0;
		
		zpos++;
		if (zpos < 4) return;
		zpos = 0;
		
		System.err.println("Warning: End of disk image has been reached!");
		
	}

}
