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

public class UCICompiler {
	
	private final Map<String, String> conversions = new HashMap<String, String>();
	private final int instructionLength = 32;
	private final String commentChar = "#";
	
	public UCICompiler()
	{
		//instruction conversions
		conversions.put("ADD",   "000");
		conversions.put("AND",   "001");
		conversions.put("SUB",   "010");
		conversions.put("ADDI",  "011");
		conversions.put("SRI",   "100");
		conversions.put("STORE", "101");
		conversions.put("LOAD",  "110");
		conversions.put("JMP",   "111");
		
		//register conversions
		conversions.put("R0",   "000");
		conversions.put("R1",   "001");
		conversions.put("R2",   "010");
		conversions.put("R3",   "011");
		conversions.put("R4",   "100");
		conversions.put("R5",   "101");
		conversions.put("R6",   "110");
		conversions.put("R7",   "111");
	}
	
	public List<String> compileFile(String fileName) throws Exception
	{
		Path filePath = Paths.get(fileName);
		
		if (!Files.exists(filePath)) {
			throw new Exception("File " + fileName + " doesn't exist");
		}
		
		List<String> allLines = Files.readAllLines(filePath);
		return compile(allLines);
	}
	
	private List<String> compile(List<String> allLines) throws Exception
	{
		List<String> wholeProgram = new ArrayList<String>();
		
		int index = 0;
		for (String line : allLines) {
			
			//remvoe comments
			String lineWithoutComment = removeAnyComments(line);
			
			//ignore empty lines
			if (lineWithoutComment.trim().isEmpty()) {
				continue;
			}
			
			//convert line to binary
			String binaryAssembly = convertUCIToBinaryAssembly(lineWithoutComment, index);
			
			//convert binary to hex
			String hexAssembly = binaryStringToHexString(binaryAssembly);
			
			//add line to program
			wholeProgram.add(index + " " + hexAssembly);
			
			index++;
		}
		
		return wholeProgram;
	}
	
	private String convertUCIToBinaryAssembly(String line, int lineIndex) throws Exception
	{
		String[] commands = line.split(" ");
		int cmdLength = instructionLength;
		String assemblyCommand = "";
		for (String command : commands) {
			
			if (cmdLength <= 0) {
				throw new Exception("Line " + lineIndex + " has syntax error");
			}
			//it's assumed that a number in an assembly line will always be the last command
			//which means that an assembly command can only contain 1 number
			if (command.matches("\\d*")) {
				//as it's the last string use the rest of the instruction bits to write the number
				assemblyCommand += numberStringToBinaryString(command, cmdLength);
				cmdLength = 0;
				//if command is found then it should be replaced with the binary value
			} else if (conversions.containsKey(command)) {
				assemblyCommand += conversions.get(command);
				cmdLength -= conversions.get(command).length();
				//if command is neither a number or a replacable then it's an error
			} else {
				throw new Exception("Syntax error on line " + lineIndex);
			}
		}
		
		//if all instruction bits wasn't used then add 0 for the rest
		if (cmdLength > 0)
		{
			assemblyCommand += String.format("%0" + cmdLength + "d", 0);
		}
		
		return assemblyCommand;
	}
	
	private String removeAnyComments(String line)
	{
		if (line.isEmpty()) {
			return line;
		}
		
		if (!line.contains(commentChar)) {
			return line;
		}
		
		int commentCharIndex = line.indexOf(commentChar);
		return line.substring(0, commentCharIndex);
	}
	
	private String numberStringToBinaryString(String number, int stringLength)
	{
		return String.format("%0" + stringLength + "d", Integer.valueOf(Integer.toBinaryString(Integer.valueOf(number))));
	}
	
	private String binaryStringToHexString(String binary)
	{
		return new BigInteger(binary, 2).toString(16);
	}
	
	public void saveProgram(List<String> programLines, String filename) throws IOException
	{
		Path file = Paths.get(filename);//args[0].split("\\.")[0] + ".UCI");
		Files.write(file, programLines, Charset.forName("UTF-8"));
	}
}
