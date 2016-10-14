import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import UCI.CompilerFlags;
import UCI.CompilerOutputOptions;
import UCI.UCICompiler;

public class UCIC {
	
	
	public static void main(String[] args) {	
		
		if (args.length == 0) {
			System.out.println("Missing filename as argument");
			return;
		}
				
		
		
		try {
			UCICompiler compiler = new UCICompiler();
			
			CompilerOutputOptions cOutputOption = CompilerFlags.getCompilerOutputOption(args);
			
			List<String> assembly = compiler.compileFile(args[0], cOutputOption);
			
			String savePath = getSavePath(CompilerFlags.removeFlagsFromArguments(args));
			
			
			compiler.saveProgram(assembly, savePath);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
		}
	}
	
	private static String getSavePath(String[] args)
	{
		String savePath;
		if (args.length == 2) {
			savePath = args[1];
		}
		else {
			//if contains extension then remove it and add .asm
			//only remove the last extension as there can be more than one
			if (args[0].contains("."))
			{
				savePath = args[0].substring(0, args[0].lastIndexOf(".")) + ".asm";
			}
			//else just add .asm
			else {
				savePath = args[0] + ".asm";
			}
		}
		return savePath;
	}
}
