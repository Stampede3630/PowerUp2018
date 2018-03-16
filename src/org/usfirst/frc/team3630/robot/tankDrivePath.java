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
import edu.wpi.first.wpilibj.smartdashboard.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

public class tankDrivePath {
	public AHRS ahrs;
	private TankModifier _modifier;
	Trajectory leftTrajectory;
	Trajectory rightTrajectory;
	private TalonSRX lTalon;
	private TalonSRX rTalon;
	public EncoderFollower lEncoderFollower, rEncoderFollower;
	double setLeftMotors, setRightMotors;
	boolean sanitaryMoters, unSainitaryMoters;
	
	public tankDrivePath(TalonSRX leftSRXSide, TalonSRX rightSRXSide) {
		ahrs = new AHRS(SPI.Port.kMXP); 
		ahrs.reset();
		lTalon = leftSRXSide;
		rTalon = rightSRXSide;
		//rTalon.setSensorPhase(true);
		sanitaryMoters = false;
		unSainitaryMoters = false;
	
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
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC,Trajectory.Config.SAMPLES_HIGH, 0.05,5 , 100, 50);// are theese sane		//Generates points for the path
		/**
		 * waypoints are rewuired to have
		 * x and y for a angle 
		 * + y leftHand , -Y rightHand, +x robot forward in respect of going down game feild, 
		 * +angle goes counterclockiwise so invert navx yaw
		 * sample leagale waypoint  === new Waypoint(4, 1.0, 0),
		 */
		Waypoint[] points = new Waypoint[] {

				

				// new Waypoint(-4, -1, Pathfinder.d2r(-45)),
				new Waypoint(0, 0, 0),
				new Waypoint(6.1, 0, 0),


				//new Waypoint(2, 4.5 , Pathfinder.d2r(60)) // getts us close to 60 
				//new Waypoint(4, -1.524, Pathfinder.d2r(-90))  // got close to 9o robot at -73.4 yow  Waypoint(1, 4, Pathfinder.d2r(90))

		};

		Trajectory trajectory = Pathfinder.generate(points, config);

		_modifier = new TankModifier(trajectory).modify(0.7366);
		

		leftTrajectory = _modifier.getLeftTrajectory();
		rightTrajectory = _modifier.getRightTrajectory();
		
		
		// reset encoders in driveTrain 

		lEncoderFollower = new EncoderFollower(leftTrajectory);
		rEncoderFollower = new EncoderFollower(rightTrajectory);
	/**
	 * peramiters enc starting point, total amount ticks, wheel diamitor
	 */
		
		lEncoderFollower.configureEncoder(0, 238, 0.1524 );
		rEncoderFollower .configureEncoder(0, 238,  0.1524 );

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

			System.out.print(leftTrajectory.get(i).acceleration); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).dt); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).heading); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).jerk); System.out.print(",");
			System.out.print(leftTrajectory.get(i).position); System.out.print(",");
			System.out.print(leftTrajectory.get(i).velocity ); System.out.print(",");
			System.out.print(leftTrajectory.get(i).velocity * (1)); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).x); System.out.print(",");
			//System.out.print(leftTrajectory.get(i).y);
			System.out.print("\n");

		}
		
		// helpful note kv needs to be a decim no (
		lEncoderFollower.configurePIDVA( .8, 0 ,0  , (.1), 0.0);
		rEncoderFollower.configurePIDVA(.8, 0 ,0  , (.1) , 0.0) ;


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
	public int getDistance_ticks(TalonSRX _talon) {
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
	}

	
	public void autoPeriodic() {
	
		double outputLeft = lEncoderFollower.calculate(getDistance_ticks(lTalon));
		double outputRight = rEncoderFollower.calculate(getDistance_ticks(rTalon));
		//	for gyro functionality add 	//+turn
		 setLeftMotors= outputLeft  ;
		//	for gyro functionality 	//-turn
		 
		 
		 // times by -1 to get robot wheels to move in same direction
		 setRightMotors = outputRight *-1;
		pathDiog();

	
	 // checks if motor output is sanitary
	 
	 if (outputLeft>=1 || outputLeft<=-1 ) {
		System.out.println("Unsanitary talon output");
		System.out.println(outputLeft);
		unSainitaryMoters = true;

	}	
	
	else if (outputLeft<=1 || outputLeft>=-1 ){
		System.out.println("sanitary talon output");
		System.out.println(outputLeft);
		sanitaryMoters = true;
	}
	
	lTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, setLeftMotors);
	rTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, setRightMotors);
}

	
}

