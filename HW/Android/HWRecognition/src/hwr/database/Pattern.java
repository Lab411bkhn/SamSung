package hwr.database;

public class Pattern {
	public String value;
	public int NOP;
	public double[] target;
	public Pattern()
	{
	}
	public Pattern(String _value, int _NOP, double[] _target)
	{
		value = _value;
		NOP = _NOP;
		target = _target;
	}
}
