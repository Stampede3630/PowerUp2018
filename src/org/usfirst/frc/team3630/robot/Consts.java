package org.usfirst.frc.team3630.robot;

public class Consts {
	
	// naming file convention can't reemeb
	
	// public static final int????
	
	// class secions  or organize by other catagories
    // global assignments /
	public static final int xBoxComPort = 0; 
	// DriveTrain 
	public static final int frontLeftTalon = 1;
	public static final int backLeftTalon = 3;
	public static final int frontRightTalon = 4; 
	public static final int backRightTalon = 2;
	
	// Manipulators 
	
	
	public static final double  kPA = 0.1;

	public static final double  kIA = 0;
	
	public static final double  kID = 0;
	

    public static final double kToleranceDegrees = .5f;    
	
	//Wheel Measurements
	public static final int ticksPerRotation = 1000;
	public static final int millisecondsPerSecond = 1000;
	public static final int timeOutMs = 10;
	
	//PID encoders drive number of rotations
	public static final double kPencoder = 1.7;
	public static final double kIencoder = 0.0;
	public static final double kDencoder = 0.0;

	
	
	
	
}
