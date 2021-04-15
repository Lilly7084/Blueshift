package blueshift.actions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import blueshift.util.StreamReader;
import blueshift.util.StreamWriter;

public class LinkObjects {
	
	public static void run(List<String> args, Map<String, String> ops) throws Exception {
		if (args.size() < 3) {
			System.err.println("Insufficient arguments: Requires at least 2");
			return;
		}
		
		// Get list of objects
		List<File> objects = new ArrayList<File>();
		for (File f : new File(System.getProperty("user.dir")).listFiles()) {
			String[] foo = f.getName().split("\\.");
			if (foo[foo.length - 1].equals("exa_obj")) {
				objects.add(f);
			}
		}
		
		int lineCount = 0;
		List<Byte> data = new ArrayList<Byte>();
		
		// Process each object
		for (File f : objects) {
			
			// Read object
			byte[] rawData = Files.readAllBytes(f.toPath());
			StreamReader sr = new StreamReader(rawData);
			
			// Read object line count
			lineCount += sr.readInt();
			
			// Read EXA name
			String name = new String(new byte[] {
					sr.readByte(), sr.readByte()
			});
			
			// Read metadata
			int[] metadata = new int[104];
			for (int i = 0; i < 13; i++) {
				byte foo = sr.readByte();
				for (int j = 0; j < 8; j++) {
					metadata[i * 8 + j] = (foo >> (7 - j)) & 1;
				}
			}
			
			// Read code
			String code = sr.readString();
			
			// Reassemble agent data
			StreamWriter sw = new StreamWriter();
			sw.writeByte((byte) 0x0A);
			sw.writeString(name);
			sw.writeString(code);
			sw.writeByte((byte) (
					metadata[100] << 1 |
					metadata[101]
			));
			sw.writeByte((byte) metadata[102]);
			for (int i = 0; i < 100; i++) {
				sw.writeByte((byte) metadata[i]);
			}
			
			// Flush agent data
			data.addAll(sw.data);
			
		}
		
		// Write solution header
		StreamWriter sw = new StreamWriter();
		sw.writeInt(0x000003EF);
		sw.writeString("PB039");
		String solutionName = args.get(2).toUpperCase();
		sw.writeString(solutionName);
		sw.writeInt(0);
		sw.writeInt(lineCount);
		sw.writeTable(new int[] { });
		sw.writeInt(objects.size());
		
		// Write solution data
		for (byte foo : data) {
			sw.writeByte(foo);
		}
		
		// Write solution file
		Path output = new File(args.get(1)).toPath();
		byte[] foo = new byte[sw.data.size()];
		for (int i = 0; i < foo.length; i++) {
			foo[i] = sw.data.get(i);
		}
		Files.write(output, foo);
		
	}

}
