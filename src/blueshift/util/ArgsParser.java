package blueshift.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgsParser {
	
	private List<String> arguments = new ArrayList<String>();
	private Map<String, String> operators = new HashMap<String, String>();
	
	public ArgsParser(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("--")) { // String operators
				String[] foo = arg.substring(2).split("=");
				if (foo.length == 1) { // No equal signs; set to true
					operators.put(foo[0].toLowerCase(), "true");
				} else { // One or more equal signs; set all operators
					String value = foo[foo.length - 1];
					for (int i = 0; i < foo.length - 1; i++) {
						operators.put(foo[i].toLowerCase(), value);
					}
				}
			} else if (arg.startsWith("-")) { // Char operators
				for (String foo : arg.split("")) { // Set each char to true
					operators.put(foo, "true");
				}
			} else { // Arguments
				arguments.add(arg);
			}
		}
	}
	
	public List<String> args() {
		return arguments;
	}
	
	public Map<String, String> ops() {
		return operators;
	}

}
