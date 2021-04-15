package blueshift.actions;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import blueshift.util.DiskWriter;
import blueshift.util.LabelMaker;
import blueshift.util.StreamReader;

public class PackDisk {
	
	public static void run(List<String> args, Map<String, String> ops) throws Exception {
		if (args.size() < 2) {
			System.err.println("Insufficient arguments: Requires at least 1");
			return;
		}
		
		// Read solution
		byte[] data = Files.readAllBytes(new File(args.get(1)).toPath());
		
		// Parse out solution name
		StreamReader reader = new StreamReader(data);
		reader.readInt(); // Image magic
		reader.readString(); // Level ID
		String name = reader.readString().toUpperCase(); // Solution name
		
		// Set up disk writer
		BufferedImage blank = ImageIO.read(new File("src/data/BlankDisk.png"));
		Font font = Font.createFont(Font.TRUETYPE_FONT, new File("src/data/PermanentMarker.ttf"));
		font = font.deriveFont((float) 20);
		
		LabelMaker.makeLabel(name, font, blank, new int[] {
				228, 236, 236, // Max widths, from top to bottom
				229, 257, 284  // Y position, from top to bottom
		});
		DiskWriter writer = new DiskWriter(blank);
		writer.write(data);
		
		// Write disk image
		String imageName = args.get(1).split("\\.")[0] + ".png";
		ImageIO.write(writer.getImage(), "PNG", new File(imageName));
		
	}

}
