package UCI;

public class ProgramLine {

	public OperatorType oType;
	public int lineNumber;
	public final int initialLineNumber;
	public final String line;
	public final String lineWithoutComments;
	
	public ProgramLine(int lineNumber, String line)
	{
		this.initialLineNumber = lineNumber;
		this.lineNumber = lineNumber;
		this.line = line;
		this.lineWithoutComments = Preprocessor.removeAnyComments(line);
		this.oType = getOperatorType();
	}
	
	private OperatorType getOperatorType()
	{
		//if line is empty or only filled with a comment
		if (Preprocessor.removeAnyComments(line).isEmpty())
		{
			return OperatorType.NONE;
		}
		
		if (JMP.isJMPToLine(line))
		{
			return OperatorType.JMP_TO;
		}
		
		//if none of the above then assume it's a command
		return OperatorType.COMMAND;
	}
}
