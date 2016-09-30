package hwr.signal;

public class Convert {
	public static double[] string2Array(String input)
	{
		String[] tmp = input.trim().split("\t");
		double[] output = new double[tmp.length];
		for(int i=0; i<output.length; i++)
		{
			output[i] = Double.parseDouble(tmp[i]);
		}
		return output;
	}
	public static String array2String(double[] array)
	{
		String result = "";
		for(int i=0; i<array.length; i++)
		{
			result += Double.toString(array[i]) + "\t";
		}
		return result.trim();
	}
}
