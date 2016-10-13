package Tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import UCI.JMP;
import UCI.OperatorType;
import UCI.ProgramLine;
import UCI.ExceptionTypes.SyntaxException;

public class Test
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

}
