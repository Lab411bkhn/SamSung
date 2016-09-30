package hwr.signal;

public class CubicInterpolator {
	public static double interpolate(double x0, double x1, double x2, double x3, double x)
	{
		double a = -0.5*x0 + 1.5*x1 - 1.5*x2 + 0.5*x3;
		double b = x0 - 2.5*x1 + 2*x2 - 0.5*x3;
		double c = -0.5*x0 + 0.5*x2;
		double d = x1;
		return a*x*x*x + b*x*x + c*x + d;
	}
	public static double[] reSampling(double[] oldSignal, int sizeOfNewSignal)
	{
		double[] newSignal = new double[sizeOfNewSignal];
		int sizeOfOldSignal = oldSignal.length;
		for(int i=0; i<sizeOfNewSignal; i++)
		{
			double tmp = (double) i*(sizeOfOldSignal-1)/(sizeOfNewSignal-1);
			for(int j=0; j<sizeOfOldSignal-1; j++)
			{
				double diff = tmp - j;
				if(diff >= 0 && diff <= 1)
				{
					if(j==0)
					{
						newSignal[i] = interpolate(oldSignal[j], oldSignal[j], oldSignal[j+1], oldSignal[j+2], diff);
					}
					else if(j==sizeOfOldSignal-2)
					{
						newSignal[i] = interpolate(oldSignal[j-1], oldSignal[j], oldSignal[j+1], oldSignal[j+1], diff);
					}
					else
					{
						newSignal[i] = interpolate(oldSignal[j-1], oldSignal[j], oldSignal[j+1], oldSignal[j+2], diff);
					}
				}
			}
		}
		return newSignal;
	}
}
