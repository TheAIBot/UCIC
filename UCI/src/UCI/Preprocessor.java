package UCI;

import java.util.*;

public class Preprocessor {
	
	private static final String COMMENT_CHAR = "//";

	public static List<String> removeAnyComments(List<String> programLines)
	{
		List<String> noComments = new ArrayList<String>();
		for (String line : programLines) {
			noComments.add(removeAnyComments(line));
		}
		return noComments;
	}
	
	public static String removeAnyComments(String line)
	{
		line = line.trim();
		if (line.isEmpty()) {
			return line;
		}
		
		if (!line.contains(COMMENT_CHAR)) {
			return line;
		}
		
		int commentCharIndex = line.indexOf(COMMENT_CHAR);
		return line.substring(0, commentCharIndex);
	}
	
}
