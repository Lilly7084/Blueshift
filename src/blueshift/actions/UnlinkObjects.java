package blueshift.actions;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import blueshift.util.StreamReader;
import blueshift.util.StreamWriter;

public class UnlinkObjects {
	
	public static void run(List<String> args, Map<String, String> ops) throws Exception {
		if (args.size() < 2) {
			System.err.println("Insufficient arguments: Requires at least 1");
			return;
		}
		
		// Read original solution file
		byte[] data = Files.readAllBytes(new File(args.get(1)).toPath());
		StreamReader reader = new StreamReader(data);
		
		// Read solution header
		reader.readInt(); // File magic
		reader.readString(); // Level ID
		String solutionName = reader.readString();
		System.out.println("Solution name: " + solutionName);
		reader.readInt(); // Padding?
		int lineCount = reader.readInt();
		int[] statistics = reader.readTable();
		if (statistics.length == 3) {
			lineCount = statistics[1];
		}
		System.out.println("Solution line count: " + lineCount);
		int agentCount = reader.readInt();
		System.out.println("Solution agent count: " + agentCount);
		
		// Repeat for each agent
		for (int i = 0; i < agentCount; i++) {
			
			// Read agent header
			reader.readByte(); // New line?
			String exaName = reader.readString();
			String exaCode = reader.readString();
			int editorMode = (int) reader.readByte();
			boolean messageMode = reader.readBool();
			
			// Read metadata
			resetBuffer();
			for (int j = 0; j < 100; j++) {
				writeBit(reader.readBool() ? 1 : 0);
			}
			writeBit((editorMode >> 1) & 1);
			writeBit(editorMode & 1);
			writeBit(messageMode ? 1 : 0);
			writeBit(0);
			
			// Set up writer
			StreamWriter sw = new StreamWriter();
			sw.writeInt(0); // Should be line count, unlinker doesn't know line count though
			
			// Write EXA name
			if (exaName.length() == 1) {
				sw.writeByte((byte) exaName.charAt(0));
				sw.writeByte((byte) 0);
			} else if (exaName.length() == 2) {
				sw.writeByte((byte) exaName.charAt(0));
				sw.writeByte((byte) exaName.charAt(1));
			}
			
			// Write sprite, editor mode, and message mode
			for (byte foo : metadata) {
				sw.writeByte(foo);
			}
			
			// Write code and flush
			sw.writeString(exaCode);
			byte[] exa = new byte[sw.data.size()];
			for (int j = 0; j < exa.length; j++) {
				exa[j] = sw.data.get(j);
			}
			
			// Write to file
			Files.write(new File(exaName + ".exa_obj").toPath(), exa);
			
		}
		
	}
	
	private static byte[] metadata;
	private static int byteIndex;
	private static int bitIndex;
	
	private static void resetBuffer() {
		metadata = new byte[13];
		byteIndex = 0;
		bitIndex = 7;
	}
	
	private static void writeBit(int data) {
		metadata[byteIndex] &= ~(1 << bitIndex);
		metadata[byteIndex] |= data << bitIndex;
		bitIndex--;
		if (bitIndex < 0) {
			bitIndex = 7;
			byteIndex++;
		}
	}

}
