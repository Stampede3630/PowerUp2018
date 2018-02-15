package org.usfirst.frc.team3630.robot;

public class Consts {
	
	// naming file convention can't reemeb
	
	// public static final int????
	
	// class secions  or organize by other catagories
    // global assignments /
	public static final int xBoxComPort = 0; 
	// DriveTrain 
	public static final int leftTwo = 1;
	public static final int leftThree = 2;
	public static final int leftEncoder = 3;
	public static final int rightTwo = 4; 
	public static final int rightThree = 5;
	public static final int rightEncoder = 6;
	
	// Manipulators 
	
	
	public static final double  kPRotAng = 0.1;

	public static final double  kIRotAng = 0;
	
	public static final double  kDRotAng = 0;
	

    public static final double ToleranceDegrees = 2;
    public static final double ToleranceDistance = 3;
	
	//Wheel Measurements
	public static final int ticksPerRotation = 1000;
	public static final int millisecondsPerSecond = 1000;
	public static final int timeOutMs = 10;
	public static final double wheelRadiusInch = 3;
	
	//PID encoders drive number of rotations
	public static final double kPencoder = 0.4;
	public static final double kIencoder = 0.000000000;
	public static final double kDencoder = 0.0;

	public static final double kPPos = 0.07;
	public static final double kIPos = 0;
	public static final double kDPos = 0;
	// Auto Distances
	public static final double autoA = 67;
	public static final double autoB = 101;
	public static final double autoC = 156;
	public static final double autoD = 73;
	public static final double autoE = 55.56;
	public static final double autoF = 41.88;
	public static final double autoG = 192.62;
	public static final double autoH = 264.62;
	public static final double autoI = 120.62;
	public static final double autoLine = 120;
	
	public static final double autoPosError = 3;
	public static final double autoTurnError = 2;
	
	
	
}
