package UCI;

import java.util.*;

import UCI.ExceptionTypes.SyntaxException;

public class JMP
{

	private static final String JMP_TO_PREFIX = "#";

	public static Map<String, Integer> getJMPTable(List<ProgramLine> programLines) throws SyntaxException
	{
		Map<String, Integer> jmpTable = new HashMap<String, Integer>();
		int lastNotJMPCommandLineIndex = 0;

		for (ProgramLine programLine : programLines)
		{
			if (programLine.oType != OperatorType.JMP_TO)
			{
				lastNotJMPCommandLineIndex++;
				continue;
			}
			
			int lineLength = programLine.lineWithoutComments.length() - JMP_TO_PREFIX.length();
			
			//if the length of the line is 0 then there isn't space for a name
			if (lineLength == 0)
			{
				throw new SyntaxException(programLine);
			}
			
			String jmpName = programLine.lineWithoutComments.substring(JMP_TO_PREFIX.length());
			
			jmpTable.put(jmpName, lastNotJMPCommandLineIndex);
			programLine.oType = OperatorType.NONE;
		}
		
		return jmpTable;
	}

	public static boolean isJMPToLine(String line)
	{
		return line.contains(JMP_TO_PREFIX);
	}

}
