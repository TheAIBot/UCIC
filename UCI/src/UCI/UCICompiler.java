package UCI;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import UCI.ExceptionTypes.SyntaxException;

public class UCICompiler
{

	public final Map<String, String> conversions = new HashMap<String, String>();
	public final int instructionLength = 32;

	public UCICompiler()
	{
		// instruction conversions
		conversions.put("ADD", "000");
		conversions.put("AND", "001");
		conversions.put("SUB", "010");
		conversions.put("ADDI", "011");
		conversions.put("SRI", "100");
		conversions.put("STORE", "101");
		conversions.put("LOAD", "110");
		conversions.put("JMP", "111");

		// register conversions
		conversions.put("R0", "000");
		conversions.put("R1", "001");
		conversions.put("R2", "010");
		conversions.put("R3", "011");
		conversions.put("R4", "100");
		conversions.put("R5", "101");
		conversions.put("R6", "110");
		conversions.put("R7", "111");
	}

	public List<String> compileFile(String fileName) throws Exception
	{
		Path filePath = Paths.get(fileName);

		if (!Files.exists(filePath))
		{
			throw new Exception("File " + fileName + " doesn't exist");
		}

		List<String> allLines = Files.readAllLines(filePath);
		return compile(allLines);
	}

	private List<ProgramLine> createProgramFromStrings(List<String> wholeProgram)
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

	public List<String> compile(List<String> allLines) throws Exception
	{
		List<ProgramLine> programLines = createProgramFromStrings(allLines);

		programLines = purgeEmptyLines(programLines);

		Map<String, Integer> jmpTable = JMP.getJMPTable(programLines);

		// purge old jmp to lines that was converterd to empty lines
		programLines = purgeEmptyLines(programLines);

		return compileProgramToHex(programLines, jmpTable);
	}
	
	private List<String> compileProgramToHex(List<ProgramLine> programLines, Map<String, Integer> jmpTable) throws SyntaxException
	{
		List<String> hexProgram = new ArrayList<String>();

		for (ProgramLine programLine : programLines)
		{
			// convert line to binary
			String binaryAssembly = convertUCIToBinaryAssembly(programLine, jmpTable);

			// convert binary to hex
			String hexAssembly = Converter.binaryToHex(binaryAssembly);

			// add line to program
			hexProgram.add(programLine.lineNumber + " " + hexAssembly);
		}

		return hexProgram;
	}

	private String convertUCIToBinaryAssembly(ProgramLine programLine, Map<String, Integer> jmpTable) throws SyntaxException
	{
		String[] commands = programLine.lineWithoutComments.split(" ");
		int cmdLength = instructionLength;
		String assemblyCommand = "";
		for (String command : commands)
		{
			if (cmdLength < 0)
			{
				throw new SyntaxException(programLine);
			}
			
			// it's assumed that a number in an assembly line will always be the
			// last command
			// which means that an assembly command can only contain 1 number at the end ofthe command
			if (command.matches("\\d*"))
			{
				// as it's the last string use the rest of the instruction bits
				// to write the number
				assemblyCommand += Converter.numberToBinary(Integer.valueOf(command), cmdLength);
				cmdLength = 0;
			}
			// if command is found then it should be replaced with the
			// binary value
			else if (conversions.containsKey(command))
			{
				assemblyCommand += conversions.get(command);
				cmdLength -= conversions.get(command).length();
			}
			//if command is found in jmpTable then it should be replaced with
			//the line index assosiated with the command in jmpTable
			else if (jmpTable.containsKey(command))
			{
				assemblyCommand += Converter.numberToBinary(jmpTable.get(command).intValue(), cmdLength);
				cmdLength = 0;
			}
			// if command is neither a number or a replacable then it's an
			// error
			else
			{
				throw new SyntaxException(programLine);
			}
		}
		
		if (cmdLength < 0)
		{
			throw new SyntaxException(programLine);
		}

		// if all instruction bits wasn't used then add 0 for the rest
		if (cmdLength > 0)
		{
			assemblyCommand += String.format("%0" + cmdLength + "d", 0);
		}

		return assemblyCommand;
	}

	public void saveProgram(List<String> programLines, String filename) throws IOException
	{
		Path file = Paths.get(filename);// args[0].split("\\.")[0] + ".UCI");
		Files.write(file, programLines, Charset.forName("UTF-8"));
	}
}
