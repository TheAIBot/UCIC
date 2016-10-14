package UCI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import UCI.ExceptionTypes.InvalidInstructionFormat;

public class Instruction
{	
	private static final int INSTRUCTION_LENGTH = 32;
	
	public final InstructionBlock[] blockFormat;
	
	public Instruction(String[] format) throws InvalidInstructionFormat
	{
		this.blockFormat = getInstructionFormat(format);
	}
	
	private InstructionBlock[] getInstructionFormat(String[] format) throws InvalidInstructionFormat
	{
		List<InstructionBlock> iBlocks = new ArrayList<InstructionBlock>();
		int instructionLength = INSTRUCTION_LENGTH;
		for (int i = 0; i < format.length; i++)
		{
			String formatPart = format[i];
			
			if (formatPart.matches(InstructionBlockType.CONSTANT.typeFormat))
			{
				int bitSize = formatPart.length();
				instructionLength -= bitSize;
				iBlocks.add(new InstructionBlock(formatPart, bitSize, InstructionBlockType.CONSTANT));
			}
			else if (formatPart.matches(InstructionBlockType.NUMBER.typeFormat))
			{
				int bitSize = Integer.valueOf(formatPart.substring(1));
				instructionLength -= bitSize;
				iBlocks.add(new InstructionBlock(formatPart, bitSize, InstructionBlockType.NUMBER));
			}
			else if (formatPart.matches(InstructionBlockType.REPLACABLE.typeFormat))
			{
				int bitSize = Integer.valueOf(formatPart.substring(1));
				instructionLength -= bitSize;
				iBlocks.add(new InstructionBlock(formatPart, bitSize, InstructionBlockType.REPLACABLE));
			}
			else {
				throw new InvalidInstructionFormat("Invalid format", format);
			}
		}
		
		if (instructionLength < 0)
		{
			throw new InvalidInstructionFormat("Too many bits designated", format);
		}
		else {
			int bitSize = instructionLength;
			char[] array = new char[instructionLength];
		    Arrays.fill(array, '0');
		    String formatPart = new String(array);
			iBlocks.add(new InstructionBlock(formatPart, bitSize, InstructionBlockType.CONSTANT));
		}
		
		return iBlocks.toArray(new InstructionBlock[iBlocks.size()]);
	}
}
