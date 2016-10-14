package UCI;

public enum InstructionBlockType
{
	NUMBER("(N|n)\\d+"),
	CONSTANT("(0|1)+"),
	REPLACABLE("(R|r)\\d+");
	
	public final String typeFormat;
	
	private InstructionBlockType(String format)
	{
		typeFormat = format;
	}
}
