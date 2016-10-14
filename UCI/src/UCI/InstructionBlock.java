package UCI;

import UCI.ExceptionTypes.SyntaxException;

public class InstructionBlock
{
	public final String format;
	public final int bitLength;
	public final InstructionBlockType type;
	
	public InstructionBlock(String format, int bitLength, InstructionBlockType type)
	{
		this.format = format;
		this.bitLength = bitLength;
		this.type = type;
	}
}
