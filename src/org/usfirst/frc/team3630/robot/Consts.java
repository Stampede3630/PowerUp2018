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
	
	//////////////////////////
	// manipulator conts 
	// pcma
	public static final int solonoidLifterOpenChanal  = 0 ;// a prime
	public static final int solonoidLifterCloseChanal  = 1 ;//b prime
	public static final int solonoidClampOpenChanal= 2;// a3
	public static final int solonoidClampCloseChanal= 3;// b2
	// pcm b
	public static final int solonoidKickOpenChanal  = 0 ;//a4
	public static final int solonoidKickCloseChanal  = 1 ;//b4
	public static final int	solonoidSliodeOpenChanal=2;
	public static final int		solonoidSlideCloseChanal = 3;
	public static final int pcmChanal0= 0;
	public static final int  pcmChanal1= 1;

	
}	

