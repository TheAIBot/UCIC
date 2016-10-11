package UCI;

import java.math.BigInteger;

public class Converter {
	
	public static String numberToBinary(int number, int stringLength)
	{
		return String.format("%" + stringLength + "s", Integer.toBinaryString(number)).replace(" ", "0");// Integer.valueOf(Integer.toBinaryString(number)));
	}
	
	public static String binaryToHex(String binary)
	{
		return new BigInteger(binary, 2).toString(16);
	}
}
