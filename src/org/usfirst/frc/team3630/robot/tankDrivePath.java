package org.usfirst.frc.team3630.robot;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.followers.DistanceFollower;
import jaci.pathfinder.modifiers.TankModifier;
import com.ctre.phoenix.*;
import java.io.File;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

public class tankDrivePath {
	public AHRS ahrs;
	private TankModifier _modifier;
	Trajectory leftTrajectory;
	Trajectory rightTrajectory;
	private WPI_TalonSRX lTalon;
	private WPI_TalonSRX rTalon;
	public EncoderFollower lEncoderFollower, rEncoderFollower;
	double setLeftMotors, setRightMotors;
	boolean sanitaryMoters, unSainitaryMoters;
	boolean finished;
	public tankDrivePath(WPI_TalonSRX leftSRXSide, WPI_TalonSRX rightSRXSide, AHRS myGyro) {
	
		ahrs = myGyro;
		ahrs.reset();
		lTalon = leftSRXSide;
		rTalon = rightSRXSide;
		//rTalon.setSensorPhase(true);
		sanitaryMoters = false;
		unSainitaryMoters = false;
			 finished = lEncoderFollower.isFinished();
	
		/**
		 *
		 * // Create the Trajectory Configuration
		// Arguments:
		// Fit Method: HERMITE_CUBIC or HERMITE_QUINTIC
		// Sample Count: SAMPLES_HIGH (100 000)
		// SAMPLES_LOW (10 000)
		// SAMPLES_FAST (1 000)
		// Time Step: 0.05 Seconds
		// Max Velocity: 45 m/sec
		// Max Acceleration: 100 m/s/s
		// Max Jerk: 100 m/s/s
		 */
		
		//.03 used to be jerk bumped it up to ensure non limiting factor
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC,Trajectory.Config.SAMPLES_HIGH, 0.05,6.5 , 1.2, .03);// are theese sane		//Generates points for the path
		/**
		 * waypoints are rewuired to have
		 * x and y for a angle 
		 * + y leftHand , -Y rightHand, +x robot forward in respect of going down game feild, 
		 * +angle goes counterclockiwise so invert navx yaw
		 * sample leagale waypoint  === new Waypoint(4, 1.0, 0),
		 */
		Waypoint[] points = new Waypoint[] {

				

				new Waypoint(0, 0, 0),
				new Waypoint(6.1, 0, 0)


				//new Waypoint(2, 4.5 , Pathfinder.d2r(60)) // getts us close to 60 
				//new Waypoint(4, -1.524, Pathfinder.d2r(-90))  // got close to 9o robot at -73.4 yow  Waypoint(1, 4, Pathfinder.d2r(90))

		};

		Trajectory trajectory = Pathfinder.generate(points, config);

		_modifier = new TankModifier(trajectory).modify(0.7366);
		

		leftTrajectory = _modifier.getLeftTrajectory();
		rightTrajectory = _modifier.getRightTrajectory();
		
		

		lEncoderFollower = new EncoderFollower(leftTrajectory);
		rEncoderFollower = new EncoderFollower(rightTrajectory);
	/**
	 * peramiters enc starting point, total amount ticks, wheel diamitor
	 */
		lTalon.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		rTalon.setSelectedSensorPosition(0, 0, Consts.timeOutMs);
		lEncoderFollower.configureEncoder(0, 335, 0.1524 );
		rEncoderFollower.configureEncoder(0, 335,  0.1524 );

		/**
		 * for loop which prints out left trajectory data can add to csv
		 
		
		
	

		
		 * 	// The first argument is the proportional gain. Usually this will be quite high
		// The second argument is the integral gain. This is unused for motion profiling
		// The third argument is the derivative gain. Tweak this if you are unhappy with
		// the tracking of the trajectory
		// The fourth argument is the velocity ratio. This is 1 over the maximum
		// velocity you provided in the
		// trajectory configuration (it translates m/s to a -1 to 1 scale that your
		// motors can read)
		// The fifth argument is your acceleration gain. Tweak this if you want to get
		// to a higher or lower speed quicker
		//(1/3.3528
		 */
		
		
		
		
		for (int i = 0; i<leftTrajectory.length(); i++){

			//System.out.print("acceleration = ");System.out.print(leftTrajectory.get(i).acceleration); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).dt); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).heading); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).jerk); System.out.print(",");
			//System.out.print("velocity = ");System.out.print(leftTrajectory.get(i).velocity ); System.out.print(",");
			//System.out.print("position = ");System.out.print(leftTrajectory.get(i).position); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).velocity * (1)); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).x); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).y);
			//System.out.print("\n");

		}
		
		// helpful note kv needs to be a decim no.1176 (
		lEncoderFollower.configurePIDVA(0, 0 ,0  , 0.25998, 0);
		rEncoderFollower.configurePIDVA(0, 0 ,0  , 0.25998, 0) ;


	}
	
	/**
	 * made trajectory for file and puts to csv. Needs to run only once to put file on robo rip
	 */
	/*
	public void makeTrajectory(File myFile ) {
		
		// Create the Trajectory Configuration
		// Arguments:
		// Fit Method: HERMITE_CUBIC or HERMITE_QUINTIC
		// Sample Count: SAMPLES_HIGH (100 000)
		// SAMPLES_LOW (10 000)
		// SAMPLES_FAST (1 000)
		// Time Step: 0.05 Seconds
		// Max Velocity: 45 m/sec
		// Max Acceleration: 100 m/s/s
		// Max Jerk: 100 m/s/s
		
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC,
				Trajectory.Config.SAMPLES_HIGH, 0.05, 3.3528, .25 , .3);
		//Generates points for the path.
		Waypoint[] points = new Waypoint[] {
				
				// + y leftHand , -Y rightHand, +x robot forward in respect of going down game feild, +angle goes counterclockiwise so invert navx yaw
				
				// new Waypoint(-4, -1, Pathfinder.d2r(-45)),
				new Waypoint(0, 0, 0),
				//new Waypoint(2, 4.5 , Pathfinder.d2r(60)) // getts us close to 60 
				new Waypoint(4.2672, -1.524, Pathfinder.d2r(-90))  // got close to 9o robot at -73.4 yow  Waypoint(1, 4, Pathfinder.d2r(90))
		};
		Trajectory trajectory = Pathfinder.generate(points, config);
		Pathfinder.writeToCSV(myFile, trajectory);
	
	}
	*/
	
	/**
	 * get trajec file allows us to retrive a trajectory
	 */
	
	/*
	public void getTrajectory(File myFile) {
		
		Trajectory readTrajectory = Pathfinder.readFromCSV(myFile) ;
		
		_modifier = new TankModifier(readTrajectory).modify(Consts.robotWidthMeters);
		leftTrajectory = _modifier.getLeftTrajectory();
		rightTrajectory = _modifier.getRightTrajectory();
		left = new EncoderFollower(leftTrajectory);
		right = new EncoderFollower(rightTrajectory);
		left.configureEncoder(0, Consts.ticksPerRotation, 0.1524);
		right.configureEncoder(0, Consts.ticksPerRotation, 0.1524);
		// configure pidva
		// The first argument is the proportional gain. Usually this will be quite high
		// The second argument is the integral gain. This is unused for motion profiling
		// The third argument is the derivative gain. Tweak this if you are unhappy with
		// the tracking of the trajectory
		// The fourth argument is the velocity ratio. This is 1 over the maximum
		// velocity you provided in the
		// trajectory configuration (it translates m/s to a -1 to 1 scale that your
		// motors can read)
		// The fifth argument is your acceleration gain. Tweak this if you want to get
		// to a higher or lower speed quicker
		
		  left.configurePIDVA(.8, 0.0, 0.0, (1/3.3528) , 0);
		  right.configurePIDVA(.8, 0.0, 0.0, (1/3.3528), 0);
	}
	
	/**
	 * restests navx  
	 */
	public void pathInit() {
		ahrs.reset();		
	
	}
	
	/**
	 * @param _talon talon for requested encoder distance
	 * @return encoder distance in ticks
	 */
	public int getDistance_ticks(WPI_TalonSRX _talon) {
		int distance_ticks = _talon.getSelectedSensorPosition(0);
		return distance_ticks;
	}

	/**
	 * diognostics for pathfinder functuions 
	 * 
	 */
	public void pathDiog(){
		//	SmartDashboard.putNumber("robot yaw", gyroheading);
		SmartDashboard.putNumber(" vLeft",   setLeftMotors);
		SmartDashboard.putNumber(" vRight", setRightMotors);
		SmartDashboard.putNumber(" encoderRight",   getDistance_ticks(lTalon));
		SmartDashboard.putNumber(" encoderLeft", getDistance_ticks(rTalon));
		SmartDashboard.putBoolean("are moter Values are sanitary", sanitaryMoters);
		SmartDashboard.putBoolean(" are moter values not sanitary", unSainitaryMoters);
		SmartDashboard.putNumber("actualVelocityRight", rTalon.getSelectedSensorVelocity(0));
		SmartDashboard.putNumber("actualVelocityLeft", lTalon.getSelectedSensorVelocity(0));
		
		//"homebrew calculate"
		int lDistance = getDistance_ticks(lTalon);
		double distance_covered = (double) lDistance / 335 * .1524;
		if(! lEncoderFollower.isFinished()) {
			Trajectory.Segment seg = lEncoderFollower.getSegment();
			double error = seg.position - distance_covered;
			double proportional = error * 1;
			double velocityCalc = seg.velocity * .33333;
			double accelerationCalc = seg.acceleration*0;
			double calculated_value = proportional + velocityCalc + accelerationCalc;
			SmartDashboard.putNumber("error", error);
			SmartDashboard.putNumber("proportional", proportional);
			SmartDashboard.putNumber("velocitycalc", velocityCalc);
			SmartDashboard.putNumber("accelerationCalc", accelerationCalc);
			SmartDashboard.putNumber("calculated value", calculated_value);
			
		//	if(setLeftMotors != calculated_value ) DriverStation.reportWarning("pathfinder Values do not align", false); 
			
		}


	}

	
	public void autoPeriodic() {
		int lDistance = getDistance_ticks(lTalon);
	
		double australianHeading = ahrs.getYaw()*-1;
		double desiredHeading = (180/Math.PI)*(lEncoderFollower.getHeading());
		double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading-australianHeading);
		// kp for angle correction will need testing bypasting it now till pvida controllers are good
		double angleCorrection =-1/10*angleDifference;
		
		double outputLeft = lEncoderFollower.calculate(lDistance);
		double outputRight = rEncoderFollower.calculate(getDistance_ticks(rTalon));
		//	for gyro functionality add 	//+turn
		 setLeftMotors= outputLeft+angleCorrection  ;
		//	for gyro functionality 	//-turn
		 
		 
		 // times by -1 to get robot wheels to move in same direction
		 setRightMotors = outputRight-angleCorrection;
		 

	
		 
	 if (outputLeft>=1 || outputLeft<=-1 ) {
		System.out.println("WARNING Unsanitary talon output");
		System.out.println(outputLeft);
		unSainitaryMoters = true;

	}	
	
	else if (outputLeft<=1 || outputLeft>=-1 ){
		System.out.println("sanitary talon output");
		System.out.println(outputLeft);
		sanitaryMoters = true;
	}
	
	lTalon.set(outputLeft);
	rTalon.set(outputRight);
}

	
}

