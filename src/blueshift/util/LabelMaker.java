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
	
	public static void makeLabel(String text, Font font, BufferedImage target, int[] config) {
		
		// We need to get a Graphics2D and FontMetrics representing the target image.
		Graphics2D g = target.createGraphics();
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		
		// Set the rendering hints for this Graphics2D
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING       , RenderingHints.VALUE_ANTIALIAS_ON               );
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING    , RenderingHints.VALUE_COLOR_RENDER_QUALITY       );
        g.setRenderingHint(RenderingHints.KEY_DITHERING          , RenderingHints.VALUE_DITHER_ENABLE              );
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS  , RenderingHints.VALUE_FRACTIONALMETRICS_ON       );
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION      , RenderingHints.VALUE_INTERPOLATION_BILINEAR     );
        g.setRenderingHint(RenderingHints.KEY_RENDERING          , RenderingHints.VALUE_RENDER_QUALITY             );
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL     , RenderingHints.VALUE_STROKE_PURE                );
		
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

}
