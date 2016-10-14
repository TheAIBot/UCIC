package UCI.ExceptionTypes;

import UCI.Instruction;

public class InvalidInstructionFormat extends Exception
{
	public InvalidInstructionFormat(String message, String[] format)
	{
		super(message + ".\n" + String.join(" ", format));
	}
}
