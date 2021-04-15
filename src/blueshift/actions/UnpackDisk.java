package blueshift.actions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import blueshift.util.DiskReader;

public class UnpackDisk {
	
	public static void run(List<String> args, Map<String, String> ops) throws Exception {
		if (args.size() < 3) {
			System.err.println("Insufficient arguments: Requires at least 2");
			return;
		}
		
		// Read image
		BufferedImage image = ImageIO.read(new File(args.get(1)));
		byte[] data = new DiskReader(image).data;
		
		// Write solution
		Files.write(new File(args.get(2)).toPath(), data);
		
	}

}
