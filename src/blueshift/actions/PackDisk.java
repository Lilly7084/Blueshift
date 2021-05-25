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

public class PackDisk {
	
	public static void run(List<String> args, Map<String, String> ops) throws Exception {
		if (args.size() < 2) {
			System.err.println("Insufficient arguments: Requires at least 1");
			return;
		}
		
		// Read solution
		byte[] data = Files.readAllBytes(new File(args.get(1)).toPath());
		
		// Read disk name
		String name = "";
		for (int i = 2; i < args.size(); i++) {
			name += " " + args.get(i);
		}
		name = name.substring(1).toUpperCase();
		
		// Set up disk writer
		// TODO add support for multiple disk blanks
		BufferedImage blank = ImageIO.read(new File("src/data/BlankDisk.png"));
		Font font = Font.createFont(Font.TRUETYPE_FONT, new File("src/data/PermanentMarker.ttf"));
		font = font.deriveFont((float) 20);
		
		LabelMaker.makeLabel(blank, font, new int[][] {
			/*
			 * TODO update this table for better looking labels.
			 * The current labels look like a bit pile of [REDACTED]
			 * because there's text clipping with the label lines,
			 * and running off the edge of the label, and I can't be
			 * bothered to find the correct values yet...
			 */
			{228, 125, 229},
			{236, 125, 257},
			{236, 125, 284}
		}, name);
		
		DiskWriter writer = new DiskWriter(blank);
		writer.write(data);
		
		// Write disk image
		String imageName = args.get(1).split("\\.")[0] + ".png";
		ImageIO.write(writer.getImage(), "PNG", new File(imageName));
		
	}

}
