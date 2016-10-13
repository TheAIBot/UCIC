package UCI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompilerFlags
{
	private static final String AS_BINARY = "--binary";
	
	public static CompilerOutputOptions getCompilerOutputOption(String[] args)
	{
		return Arrays.asList(args).stream().anyMatch(x -> x.equals(AS_BINARY)) ? CompilerOutputOptions.BINARY : CompilerOutputOptions.HEXADECIMAL;
	}
	
	public static String[] removeFlagsFromArguments(String[] args)
	{
		List<String> argsWithoutFlags = new ArrayList<String>();
		
		for (String arg : args)
		{
			switch (arg)
			{
				case AS_BINARY:
					break;
				default:
					argsWithoutFlags.add(arg);
					break;
			}
		}
		
		return argsWithoutFlags.toArray(new String[argsWithoutFlags.size()]);
	}
}
