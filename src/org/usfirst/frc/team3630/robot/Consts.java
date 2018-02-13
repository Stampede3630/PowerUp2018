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
	public static final double  kPA = 0.1;

	public static final double  kIA = 0;
	
	public static final double  kID = 0;
    //pathfinder units all needs to be in meeters
    public static final double degtoRad = ( 3.14 /180.0);
    public static final double robotWidthMeters = 29.5 *intoMeters;
	//Wheel Measurements
	public static final int ticksPerRotation = 1000;
	public static final int millisecondsPerSecond = 1000;
	public static final int timeOutMs = 10;
	public static final double rotConversion = 6* Math.PI;
	public static final double pathConversion  = .1;
	public static final double intoMeters =  0.0254;
	public static final double Weeld  = 6 *  intoMeters;
	
	public static final double pathKP  = 0.8;
	public static final double pathKI  = 0.0;
	public static final double pathKD  = 0.0;
	public static final double pathKA  = 0.0;
	public static final double pathKV  = 0.0;
	public static final double pathKF  = (1/ (132*intoMeters ));
	
	//PID encoders drive number of rotations
	public static final double kPencoder = 1.7;
	public static final double kIencoder = 0.0;
	public static final double kDencoder = 0.0;
	
	
	public static final double turnKP  = .6* (-1.0/  80);

	
	
	
	
}
