package UCI;

import UCI.ExceptionTypes.SyntaxException;

public class ProgramLine {

	public OperatorType oType;
	public int lineNumber;
	public final int initialLineNumber;
	public final String line;
	public final String lineWithoutComments;
	
	public ProgramLine(int lineNumber, String line) throws SyntaxException
	{
		this.initialLineNumber = lineNumber;
		this.lineNumber = lineNumber;
		this.line = line;
		this.lineWithoutComments = Preprocessor.removeAnyComments(line);
		this.oType = getOperatorType();
	}
	
	private OperatorType getOperatorType() throws SyntaxException
	{
		//if line is empty or only filled with a comment
		if (lineWithoutComments.isEmpty())
		{
			return OperatorType.NONE;
		}
		
		if (JMP.isJMPToLine(this))
		{
			return OperatorType.JMP_TO;
		}
		
		//if the first char is not in the alphabet then
		//there probably is an error
		//because of that every command has to start with a letter
		if (!Character.isAlphabetic(lineWithoutComments.charAt(0)))
		{
			throw new SyntaxException(this);
		}
		
		//if none of the above then assume it's a command
		return OperatorType.COMMAND;
	}
}
