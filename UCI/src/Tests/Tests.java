package Tests;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import UCI.CompilerOutputOptions;
import UCI.JMP;
import UCI.OperatorType;
import UCI.ProgramLine;
import UCI.UCICompiler;
import UCI.ExceptionTypes.SyntaxException;

public class Tests
{
	
	@org.junit.Test
	public void testJMPTable()
	{
		List<String> allLines = new ArrayList<String>();
		allLines.add("//this is a comasdasment");
		allLines.add("#Start");
		allLines.add("//thasis is a comment");
		allLines.add("//this is da comment");
		allLines.add("//this idds a comment");
		allLines.add("//this is a commaent");
		allLines.add("#start2");
		allLines.add("ADD R4 R6 R2//this is a commaent");
		allLines.add("SUB R4 R6 R2");
		allLines.add("//this is a comment");
		allLines.add("#End");
		
		List<ProgramLine> programLines = new ArrayList<ProgramLine>();
		
		for (int i = 0; i < allLines.size(); i++)
		{
			try
			{
				programLines.add(new ProgramLine(i, allLines.get(i)));
			} catch (SyntaxException e)
			{
				Assert.fail(e.getMessage());
			}
			
		}
		
		programLines.removeIf(x -> x.oType == OperatorType.NONE);
		
		for (int i = 0; i < programLines.size(); i++)
		{
			programLines.get(i).lineNumber = i;

		}
		
		assertEquals(programLines.size(), 5);

		Map<String, Integer> jmpTable = null;
		
		try
		{
			jmpTable = JMP.getJMPTable(programLines);
		} catch (SyntaxException e)
		{
			Assert.fail(e.getMessage());
		}
		
		assertEquals(jmpTable.size(),  3);
		assertEquals(jmpTable.get("Start").intValue(), 0);
		assertEquals(jmpTable.get("start2").intValue(), 0);
		assertEquals(jmpTable.get("End").intValue(), 2);
	}

	@org.junit.Test
	public void testSyntaxErrors()
	{
		assertException(() -> compileFast("1"), SyntaxException.class);
		assertException(() -> compileFast("ADDS"), SyntaxException.class);
		assertException(() -> compileFast("add"), SyntaxException.class);
		assertException(() -> compileFast("ADD R1"), SyntaxException.class);
		assertException(() -> compileFast("ADD R1 3 R3"), SyntaxException.class);
		assertException(() -> compileFast("ADD R1 R3 R3 R4"), SyntaxException.class);
		assertException(() -> compileFast("ADD R1 R3 R3 3"), SyntaxException.class);
		assertException(() -> compileFast("ADD 1 R1 R3 R3 3"), SyntaxException.class);
		assertException(() -> compileFast("ADDI R1 R3 R3"), SyntaxException.class);
		assertException(() -> compileFast("ADDI 1 R3 R3"), SyntaxException.class);
		assertException(() -> compileFast("ADDI -1 R3 R3"), SyntaxException.class);
		assertException(() -> compileFast("ADDI R1 fish R3"), SyntaxException.class);
		
		
		assertNoExceptions(() -> compileFast(" ADD R1 R3 R3"));
		assertNoExceptions(() -> compileFast("  ADD R1 R3 R3"));
		assertNoExceptions(() -> compileFast("  ADD R1 R3 R3" ));
		assertNoExceptions(() -> compileFast("  ADD R1 R3 R3"  ));
		assertNoExceptions(() -> compileFast("ADDI R1 R3 -1"));
	}
	
	private void compileFast(String program) throws Exception
	{
		compileFast(new String[] {program});
	}
	
	private void compileFast(String[] program) throws Exception
	{
		UCICompiler compiler = new UCICompiler();
		compiler.compile(Arrays.asList(program), CompilerOutputOptions.HEXADECIMAL);
	}

	private void assertException(ThrowingCaller toThrowError, Class<?> expectedError)
	{
		try
		{
			toThrowError.call();
			Assert.fail("No error was thrown");
		} catch (Exception e)
		{
			if (e.getClass() != expectedError)
			{
				e.printStackTrace();
				Assert.fail("Wrong error was thrown.\n Expected " + expectedError.getName() + ", got " + e.getClass().getName());
				
			}
		}
		
	}

	private void assertNoExceptions(ThrowingCaller canThrowError)
	{
		try
		{
			canThrowError.call();
		} catch (Exception e)
		{
			e.printStackTrace();
			//best message ever!
			Assert.fail("An error was thrown.\n Excepted no error.");
		}
	}
}










