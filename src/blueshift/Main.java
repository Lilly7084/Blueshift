package blueshift;

import blueshift.actions.LinkObjects;
import blueshift.actions.PackDisk;
import blueshift.actions.UnlinkObjects;
import blueshift.actions.UnpackDisk;
import blueshift.util.ArgsParser;
import blueshift.util.Toolbox;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		// FOR TESTING ONLY! REMOVE BEFORE EXPORTING
		args = new String[] {
				"pack",
				"Exapunks Title.solution"
		};
		
		// Help menu
		if (args.length == 0) {
			System.out.println(Toolbox.readFile("src/data/Help.txt"));
			return;
		}
		
		// Command selection
		ArgsParser parser = new ArgsParser(args);
		try {
			switch (parser.args().get(0)) {
			
			case "unpack":
			case "dump":
				UnpackDisk.run(parser.args(), parser.ops());
				break;
			
			case "pack":
				PackDisk.run(parser.args(), parser.ops());
				break;
			
			case "unlink":
				UnlinkObjects.run(parser.args(), parser.ops());
				break;
			
			case "link":
				LinkObjects.run(parser.args(), parser.ops());
				break;
			
			default:
				System.err.println("Unknown command \"" + parser.args().get(0) + "\"");
				break;
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
