package UCI;

import java.math.BigInteger;
import java.util.Arrays;

public class Converter
{

	public static String numberToBinary(int number, int stringLength)
	{
		return setBinaryStringToLength(Integer.toBinaryString(number), stringLength);
	}

	public static String binaryToHex(String binary)
	{
		return new BigInteger(binary, 2).toString(16);
	}

	public static String setBinaryStringToLength(String toResize, int length)
	{
		if (toResize.length() == length)
		{
			return toResize;
		}
		else if (toResize.length() < length)
		{
			char[] array = new char[length - toResize.length()];
			Arrays.fill(array, '0');
			return new String(array) + toResize;
		}
		else
		{
			return toResize.substring(toResize.length() - length);
		}

	}
}
