package hwr.signal;

public class Acceleration {
	public double ax, ay, az, acceleration;
	public Acceleration(double acceleration_x, double acceleration_y, double acceleration_z)
	{
		ax = acceleration_x;
		ay = acceleration_y;
		az = acceleration_z;
		acceleration = Math.sqrt(Math.pow(acceleration_x, 2) + Math.pow(acceleration_y, 2) + Math.pow(acceleration_z, 2));
	}
}
