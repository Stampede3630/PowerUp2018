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

// /home/lvuser/Pathfinder
public class TankDrivePath  {
	// Trajectory right,left
	public AHRS ahrs;
	TankModifier _modifier;
	Trajectory leftTrajectory;
	Trajectory rightTrajectory;
	private TalonSRX lTalon;
	private TalonSRX rTalon;
	public EncoderFollower left, right;
	DistanceFollower leftDiagnostics, rightDiagnostics;
	File file;
	public TankDrivePath(TalonSRX leftSRXSide, TalonSRX rightSRXSide) {
		 ahrs = new AHRS(SPI.Port.kMXP); 
		 ahrs.reset();
		 lTalon = leftSRXSide;
		 rTalon = rightSRXSide;
		 makeTrajectory("/home/lvuser/Pathfinder/test.csv" );
		 getTrajectory("/home/lvuser/Pathfinder/test.csv");
	
		

		rTalon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, Consts.timeOutMs);
		lTalon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, Consts.timeOutMs);

		
		
		
		
		
		// peramiters enc starting point, total amount ticks, wheel diamitor

		/*
		  for (int i = 0; i<leftTrajectory.length(); i++){
		 
			  System.out.print(leftTrajectory.get(i).acceleration); System.out.print(",");
			  System.out.print(leftTrajectory.get(i).dt); System.out.print(",");
			  System.out.print(leftTrajectory.get(i).heading); System.out.print(",");
			  System.out.print(leftTrajectory.get(i).jerk); System.out.print(",");
			  System.out.print(leftTrajectory.get(i).position); System.out.print(",");
			  System.out.print(leftTrajectory.get(i).velocity); System.out.print(",");
			  System.out.print(leftTrajectory.get(i).x); System.out.print(",");
			  System.out.print(leftTrajectory.get(i).y);
			  System.out.print("\n");
		  
		  }
		*/
	

		
		  System.out.print("Wheel circumfrence: ");
		  
	}
	public void makeTrajectory(String fileName ) {
		
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

		Pathfinder.writeToCSV(new File (fileName) , trajectory);
	

	}
	public void getTrajectory(String fileName ) {
		
		
		
		Trajectory readTrajectory = Pathfinder.readFromCSV(new File(fileName)) ;
		
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
	 * Iterative method that runs through path. Expected that this is called each 50ms (as expected through TimedRobot) 
	 */
	public void autoPeriodic() {

		SmartDashboard.putNumber("left encoder distance", getDistance_ticks(lTalon));
		SmartDashboard.putNumber("Right encoder distance", getDistance_ticks(rTalon));
		
		//NAVX heading
		//Since Jaci is from Australia, her compas is literally upsidedown 90 really = -90
		//Path_heading is therefore the Pathfinder turn feedback loop
		double gyro_heading =  ahrs.getYaw();  // Assuming the gyro is giving a value in degrees
		double Path_heading = -1*  ahrs.getYaw(); 
		SmartDashboard.putNumber("robot yaw", gyro_heading);
		double desired_heading = (180/Math.PI)*(left.getHeading());  // Should also be in degrees
		//boundHalf method makes sure we are in -180 to 180
		double angleDifference =  Pathfinder.boundHalfDegrees(desired_heading - Path_heading);
		double turn = Consts.turnKP * (-1.0/  80) * angleDifference;  
		
		//calculates revised left and right output based on current ticks
		//compares it to current trajectory (EncoderFollowers)
		double outputLeft = left.calculate(getDistance_ticks(lTalon));
		double outputRight = right.calculate(getDistance_ticks(rTalon));
		double setLeftMotors= outputLeft+ turn ;
		double setRightMotors = outputRight- turn  ;
		 
		
		SmartDashboard.putNumber(" vLeft",   setLeftMotors);
		SmartDashboard.putNumber(" vRight", setRightMotors);
		//Take calculated output and set talons
		//This output should be between -1 and 1... 
		//but we think Phoenix does some magic
		lTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, setLeftMotors);
		rTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, setRightMotors);
	}
}
