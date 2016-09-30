package hwr.signal;

import java.util.ArrayList;

public class ExtractFeatures {
	public static final int NOS = 100;
	public static double[] getFeatures(ArrayList<Acceleration> handwriting)
	{
		double[] result = new double[3*NOS];
		double[] acc_x = new double[handwriting.size()];
		double[] acc_y = new double[handwriting.size()];
		double[] acc_z = new double[handwriting.size()];
		for(int i=0; i<handwriting.size(); i++)
		{
			acc_x[i] = handwriting.get(i).ax;
			acc_y[i] = handwriting.get(i).ay;
			acc_z[i] = handwriting.get(i).az;
		}
		double[] newAccX = CubicInterpolator.reSampling(acc_x, NOS);
		double[] newAccY = CubicInterpolator.reSampling(acc_y, NOS);
		double[] newAccZ = CubicInterpolator.reSampling(acc_z, NOS);
		for(int i=0; i<NOS; i++)
		{
			result[i] = newAccX[i];
			result[i+NOS] = newAccY[i];
			result[i+2*NOS] = newAccZ[i];
		}
		return result;
	}
	public static double[] getFeatures(double[] acc_x, double[] acc_y, double[] acc_z)
	{
		double[] result = new double[3*NOS];
		double[] newAccX = CubicInterpolator.reSampling(acc_x, NOS);
		double[] newAccY = CubicInterpolator.reSampling(acc_y, NOS);
		double[] newAccZ = CubicInterpolator.reSampling(acc_z, NOS);
		for(int i=0; i<NOS; i++)
		{
			result[i] = newAccX[i];
			result[i+NOS] = newAccY[i];
			result[i+2*NOS] = newAccZ[i];
		}
		return result;
	}
}
