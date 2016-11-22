package UCI;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import UCI.ExceptionTypes.InvalidInstructionFormat;
import UCI.ExceptionTypes.SyntaxException;

public class UCICompiler
{
	private static final int INSTRUCTION_LENGTH = 32;

	private final Map<String, Instruction> instructions = new HashMap<String, Instruction>();
	private final Map<String, String> conversions = new HashMap<String, String>();

	public UCICompiler() throws InvalidInstructionFormat
	{
		// instruction conversions
		instructions.put("ADD",    new Instruction(new String[] {"000", "R3", "R3", "R3"}));
		instructions.put("AND",    new Instruction(new String[] {"001", "R3", "R3", "R3"}));
		instructions.put("SUB",    new Instruction(new String[] {"010", "R3", "R3", "R3"}));
		instructions.put("ADDI",   new Instruction(new String[] {"011", "R3", "R3", "000", "N20"}));
		instructions.put("SRI",    new Instruction(new String[] {"100", "R3", "R3", "000", "N20"}));
		instructions.put("STORE",  new Instruction(new String[] {"101", "000","R3", "R3",  "R2",  "R18"}));
		instructions.put("LOAD",   new Instruction(new String[] {"110", "R3", "R3", "000", "00000000000000", "10",  "R4"}));
		instructions.put("LOADASY",new Instruction(new String[] {"110", "000","R3", "000", "00000000000000", "00",  "R4"}));
		instructions.put("LOADAWT",new Instruction(new String[] {"110", "R3", "000","000", "00000000000000", "01",  "R4"}));
		instructions.put("COP",    new Instruction(new String[] {"110", "R3", "R3", "000", "00000000000000", "10", "0101"}));
		instructions.put("COPASY", new Instruction(new String[] {"110", "000","R3", "000", "00000000000000", "00", "0101"}));
		instructions.put("COPAWT", new Instruction(new String[] {"110", "R3", "000","000", "00000000000000", "01", "0101"}));
		instructions.put("JMP",    new Instruction(new String[] {"111", "000000000", 	  "N20"}));

		// register conversions
		conversions.put("R0", "000");
		conversions.put("R1", "001");
		conversions.put("R2", "010");
		conversions.put("R3", "011");
		conversions.put("R4", "100");
		conversions.put("R5", "101");
		conversions.put("R6", "110");
		conversions.put("R7", "111");
		
		conversions.put("SENSOR", "0001"); //1
		conversions.put("MEM", 	  "0010"); //2
		conversions.put("LCD",    "0011"); //3
		conversions.put("CACHE",  "0100"); //4
		conversions.put("COP_CPU","0101"); //5
		conversions.put("COP_BUS","0110"); //6
		
		conversions.put("ASYNC",  "00");
		conversions.put("AWAIT",  "01");
		conversions.put("STALL",  "10");
		
		
	}

	public List<String> compileFile(String fileName, CompilerOutputOptions cOutputOption) throws Exception
	{
		Path filePath = Paths.get(fileName);

		if (!Files.exists(filePath))
		{
			throw new Exception("File " + fileName + " doesn't exist");
		}

		List<String> allLines = Files.readAllLines(filePath);
		return compile(allLines, cOutputOption);
	}

	private List<ProgramLine> createProgramFromStrings(List<String> wholeProgram) throws SyntaxException
	{
		List<ProgramLine> programLines = new ArrayList<ProgramLine>();

		int lineIndex = 0;
		for (String line : wholeProgram)
		{
			programLines.add(new ProgramLine(lineIndex, line));

			lineIndex++;
		}

		return programLines;
	}

	private List<ProgramLine> purgeEmptyLines(List<ProgramLine> programLines)
	{
		// remove empty lines
		programLines.removeIf(x -> x.oType == OperatorType.NONE);
		// reset line indexes
		for (int i = 0; i < programLines.size(); i++)
		{
			programLines.get(i).lineNumber = i;

		}

		return programLines;
	}

	public List<String> compile(List<String> allLines, CompilerOutputOptions cOutputOption) throws Exception
	{
		List<ProgramLine> programLines = createProgramFromStrings(allLines);

		programLines = purgeEmptyLines(programLines);

		Map<String, Integer> jmpTable = JMP.getJMPTable(programLines);

		// purge old jmp to lines that was converterd to empty lines
		programLines = purgeEmptyLines(programLines);

		List<String> compiledProgram = (cOutputOption == CompilerOutputOptions.BINARY) ? compileProgramToBinary(programLines, jmpTable) : compileProgramToHex(programLines, jmpTable);
		
		
		return addHexLineNumber(compiledProgram);
	}
	
	private List<String> compileProgramToHex(List<ProgramLine> programLines, Map<String, Integer> jmpTable) throws Exception
	{
		return compileProgramToBinary(programLines, jmpTable).stream()
															 .map(x -> Converter.binaryToHex(x))
															 .collect(Collectors.toList());
	}
	
	private List<String> compileProgramToBinary(List<ProgramLine> programLines, Map<String, Integer> jmpTable) throws Exception
	{
		List<String> binaryProgram = new ArrayList<String>();

		for (ProgramLine programLine : programLines)
		{
			// convert line to binary
			binaryProgram.add(convertUCIToBinaryAssembly(programLine, jmpTable));
		}
		return binaryProgram;
	}
	
	private List<String> addHexLineNumber(List<String> program)
	{
		List<String> programWithLineNumber = new ArrayList<String>();

		for (int i = 0; i < program.size(); i++)
		{
			String hexLineNumber = Integer.toHexString(i);
			programWithLineNumber.add(hexLineNumber  + " " + program.get(i));
		}

		return programWithLineNumber;
	}

	private String convertUCIToBinaryAssembly(ProgramLine programLine, Map<String, Integer> jmpTable) throws Exception
	{
		String[] commands = programLine.lineWithoutComments.split(" ");
		
		if (!instructions.containsKey(commands[0]))
		{
			throw new SyntaxException(programLine);
		}
		
		Instruction instruction = instructions.get(commands[0]);
		
		if (commands.length - 1 != Arrays.asList(instruction.blockFormat).stream().filter(x -> x.type != InstructionBlockType.CONSTANT).count())
		{
			throw new SyntaxException(programLine);
		}
		
		String assemblyCommand = "";
		int commandIndex = 1;
		for (InstructionBlock instructionBlock : instruction.blockFormat)
		{			
			if (instructionBlock.type == InstructionBlockType.CONSTANT)
			{
				assemblyCommand += instructionBlock.format;
			}
			else if (instructionBlock.type == InstructionBlockType.REPLACABLE)
			{
				String command = commands[commandIndex];
				
				if (!conversions.containsKey(command))
				{
					throw new SyntaxException(programLine);
				}
				
				assemblyCommand += Converter.setBinaryStringToLength(conversions.get(command), instructionBlock.bitLength);
				commandIndex++;
			}
			else if (instructionBlock.type == InstructionBlockType.NUMBER)
			{
				String command = commands[commandIndex];
				
				if (command.matches("^[+-]?\\d+$"))
				{
					assemblyCommand += Converter.numberToBinary(Integer.valueOf(command), instructionBlock.bitLength);
				}
				else if (jmpTable.containsKey(command))
				{
					assemblyCommand += Converter.numberToBinary(jmpTable.get(command).intValue(), instructionBlock.bitLength);
				}
				else 
				{
					throw new SyntaxException(programLine);
				}
				
				commandIndex++;
			}
		}
		
		if (assemblyCommand.length() != INSTRUCTION_LENGTH)
		{
			throw new UnexpectedException("Instruction length doeesn't match the expected length");
		}

		return assemblyCommand;
	}

	public void saveProgram(List<String> programLines, String filename) throws IOException
	{
		Path file = Paths.get(filename);// args[0].split("\\.")[0] + ".UCI");
		Files.write(file, programLines, Charset.forName("UTF-8"));
	}
}
