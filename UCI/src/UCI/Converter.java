package UCI;

import java.math.BigInteger;

public class Converter {
	
	public static String numberStringToBinaryString(String number, int stringLength)
	{
		return numberToBinaryString(Integer.valueOf(number), stringLength);
	}
	
	public static String numberToBinaryString(int number, int stringLength)
	{
		return String.format("%0" + stringLength + "d", Integer.valueOf(Integer.toBinaryString(number)));
	}
	
	public static String binaryStringToHexString(String binary)
	{
		return new BigInteger(binary, 2).toString(16);
	}

}
