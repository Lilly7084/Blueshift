package blueshift.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class Toolbox {
	
	// Only dependency: Main, for printing help file
	public static String readFile(String path) {
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String buffer = "";
			while (reader.ready()) {
				buffer = buffer + "\n" + reader.readLine();
			}
			reader.close();
			return buffer.substring(1);
			
		} catch (IOException e) {
			e.printStackTrace();
			return "Something went wrong while reading the file. :(";
		}
	}

	// This function isn't even used anymore, but I don't want to remove it yet.
	@Deprecated
	public static void writeFile(String data, String path) {
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(data);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int fletcher16(byte[] data) {
		int accLow = 0;
		int accHigh = 0;
		for (byte foo : data) {
			accLow = (accLow + Byte.toUnsignedInt(foo)) % 255;
			accHigh = (accHigh + accLow) % 255;
		}
		return accLow + (accHigh << 8);
	}
	
	public static byte[] deflate(byte[] data) {
		try {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Make a container for the compressed data
			DeflaterOutputStream dos = new DeflaterOutputStream(baos, true); // Wrap a deflater around it
			dos.write(data, 0, data.length); // Push the data through the deflater
			dos.flush();
			return baos.toByteArray(); // Read out the compressed data
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] inflate(byte[] data) {
		try {
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();    
	        try (OutputStream ios = new InflaterOutputStream(os)) {
	            ios.write(data);    
	        }
	        return os.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// This function isn't even used anymore, but I don't want to remove it yet.
	@Deprecated
	public static int overlayRGB(int rgb1, int rgb2) {
		
		int alpha1 = (rgb1 >> 24) & 0xFF;
		int red1 = (rgb1 >> 16) & 0xFF;
		int green1 = (rgb1 >> 8) & 0xFF;
		int blue1 = rgb1 & 0xFF;
		
		int alpha2 = (rgb2 >> 24) & 0xFF - alpha1;
		int red2 = (rgb2 >> 16) & 0xFF;
		int green2 = (rgb2 >> 8) & 0xFF;
		int blue2 = rgb2 & 0xFF;

		int red   = (  red1 * alpha1 +   red2 * alpha2) / (alpha1 + alpha2);
		int green = (green1 * alpha1 + green2 * alpha2) / (alpha1 + alpha2);
		int blue  = ( blue1 * alpha1 +  blue2 * alpha2) / (alpha1 + alpha2);
		int rgb = 0xFF000000 | (red << 16) | (green << 8) | blue;
		
		return rgb;
		
	}

}
