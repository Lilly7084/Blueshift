package blueshift.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class LabelMaker {
	
	public static void makeLabel(BufferedImage image, Font font, int[][] lines, String text) {
		
		/*
		 * DISCLAIMER: I have no clue if this is the fastest or simplest way to do this, and I'm sure
		 * that the people of GitHub will have a billion ways to make this code better. I'm all for
		 * that, in fact that's the main reason I put this code onto GitHub. As such, when I mark a
		 * line with "JANK" that's your invitation to look for, and implement, a better solution.
		 */
		
		// Set up graphics environment for image
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.setFont(font);
		setRenderingHints(g);
		FontMetrics fm = g.getFontMetrics();
		
		// Set up variables for word wrapping
		String[] words = text.split(" ");
		String line = "";
		int currentLine = 0;
		int remainingWidth = lines[0][0];
		
		// Begin word wrapping
		for (int i = 0; i < words.length; i++) {
			String word = " " + words[i];
			
			// Calculate how much space you need to fit the next word
			int requiredWidth = fm.stringWidth(word);
			
			// If the next word won't fit on the current line...
			if (remainingWidth < requiredWidth) {
				
				// Print the current line to the image
				printLine(line, lines[currentLine], fm, g);
				
				// Clear the line buffer
				line = "";
				
				// Move to the next line
				currentLine++;
				
				// Make sure this line exists on the image
				if (currentLine >= lines.length) {
					System.err.println("WARNING: No more space on disk label!");
					break;
				}
				
				// Update the remaining width
				remainingWidth = lines[currentLine][0];
				
			}
			
			// We now know the word will fit onto the current line
			// Add the word onto the line buffer
			line += word;
			
			// Update the remaining width
			remainingWidth -= requiredWidth;
			
		}
		
		// There may still be something in the line buffer
		// This needs to be printed onto the image as well,
		// provided you have room left on the label
		if (currentLine < lines.length) {
			printLine(line, lines[currentLine], fm, g);
		}
		
	}
	
	@Deprecated
	public static void makeLabel(String text, Font font, BufferedImage target, int[] config) {
		
		// We need to get a Graphics2D and FontMetrics representing the target image.
		Graphics2D g = target.createGraphics();
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		
		// Set the rendering hints for this Graphics2D
		setRenderingHints(g);
		
		String[] words = text.split(" ");
		List<String> lines = new ArrayList<String>();
		int wordIdx = 0;
		int rowIdx = 0;
		
		while (wordIdx < words.length) { 
			String buffer = "";
			
			// Pick up as many words as will fit in the row
			while (wordIdx < words.length) {
				String newBuffer = buffer + " " + words[wordIdx++];
				if (fm.stringWidth(newBuffer + " ") >= config[rowIdx]) {
					wordIdx--;
					break;
				}
				buffer = newBuffer;
			}
			
			rowIdx++;
			lines.add(buffer);
			
		}
		
		rowIdx = config.length / 2; // Skip width data, move to Y position data
		if (lines.size() == 1) {
			rowIdx++; // Skip first line for single-line labels
		}
		
		for (String line : lines) {
			
			// Calculate position on image
			int px = (target.getWidth() - fm.stringWidth(line + " ")) / 2;
			// CAN'T USE! This assumes row position refers to ascender line!
			//int py = rows[rowIdx++] + fm.getAscent();
			int py = config[rowIdx++] - fm.getDescent();
			
			// Place text on screen
			g.setColor(Color.BLACK);
			g.drawString(line.substring(1), px, py);
			
		}
		
		g.dispose();
		
	}
	
	private static void setRenderingHints(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING       , RenderingHints.VALUE_ANTIALIAS_ON               );
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING    , RenderingHints.VALUE_COLOR_RENDER_QUALITY       );
        g.setRenderingHint(RenderingHints.KEY_DITHERING          , RenderingHints.VALUE_DITHER_ENABLE              );
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS  , RenderingHints.VALUE_FRACTIONALMETRICS_ON       );
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION      , RenderingHints.VALUE_INTERPOLATION_BILINEAR     );
        g.setRenderingHint(RenderingHints.KEY_RENDERING          , RenderingHints.VALUE_RENDER_QUALITY             );
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL     , RenderingHints.VALUE_STROKE_PURE                );
	}
	
	private static void printLine(String line, int[] region, FontMetrics fm, Graphics2D g) {
		int px = region[1] - fm.stringWidth(line + " ") / 2;
		int py = region[2];
		g.drawString(line, px, py);
	}

}
