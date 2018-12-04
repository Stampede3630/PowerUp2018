package org.usfirst.frc.team3630.robot;

public class Constants 
{
	public static final int timeoutMs = 10;
	
	//gyro
	public static boolean turnToAngle;
	public static final double kP = 0.85; //0.45
	public static final double kI = 0.0;
	public static final double kD = 0.02; //keep this at 0.5 for now
	public static final double kF = 0.0;
	public static final double kA = 0.0;
	public static final double kJ = 0.0;
	public static final double maxSpeed = 1.7; //in m/s
	
	public static final double kTolarenceAngles = 2f;

	public static final double kToleranceIn = 2;
	
	public static final double wheelRadius = 3; //in
	public static final int ticksPerRotation = 330;
	
	public static final double wheelBase = 0.78; //in meters
}
