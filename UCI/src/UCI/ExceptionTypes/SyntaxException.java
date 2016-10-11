package UCI.ExceptionTypes;

import UCI.ProgramLine;

public class SyntaxException extends Exception
{
	public SyntaxException(ProgramLine programLine)
	{
		super("Syntax error on line " + programLine.initialLineNumber + "\n\"" + programLine.line + "\"");
	}
}
