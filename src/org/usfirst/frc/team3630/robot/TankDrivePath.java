package org.usfirst.frc.team3630.robot;


import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.followers.DistanceFollower;
import jaci.pathfinder.modifiers.TankModifier;
import com.ctre.phoenix.*;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;


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
	
	public TankDrivePath(TalonSRX leftSRXSide, TalonSRX rightSRXSide) {\
		// reseting ahrs 
		// should add in some recalibarting 
		 ahrs = new AHRS(SPI.Port.kMXP); 
		 ahrs.reset();
		lTalon = leftSRXSide;
		rTalon = rightSRXSide;
		
		
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
				Trajectory.Config.SAMPLES_HIGH, 0.05, 3.3528, .5, .3);

		//Generates points for the path.
		Waypoint[] points = new Waypoint[] {
				  new Waypoint(4, 1, Pathfinder.d2r(-45)),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
				    new Waypoint(2, 2, 0),                        // Waypoint @ x=-2, y=-2, exit angle=0 radians
				    new Waypoint(0, 0, 0)                           // Waypoint @ x=0, y=0,   exit angle=0 radians
				};
			
		};

		Trajectory trajectory = Pathfinder.generate(points, config);

		_modifier = new TankModifier(trajectory).modify(Consts.robotWidthMeters);


		leftTrajectory = _modifier.getLeftTrajectory();
		rightTrajectory = _modifier.getRightTrajectory();
		System.out.println("trajectories generated");

		rTalon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, Consts.timeOutMs);
		lTalon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, Consts.timeOutMs);

		left = new EncoderFollower(leftTrajectory);
		
		right = new EncoderFollower(rightTrajectory);
		
		leftDiagnostics = new DistanceFollower (leftTrajectory);
		
		// peramiters enc starting point, total amount ticks, wheel diamitor
		
		// verify
		left.configureEncoder(0, Consts.ticksPerRotation, 0.1524);
		right.configureEncoder(0, Consts.ticksPerRotation, 0.1524);
		
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
		
		// set encoders

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
		
		System.out.print("Wheel circumfrence: ");
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

		// get desired output . calucate coverts ticks to inches based on input

		// output should be between -1 and 1 correct? should check output and print it
		// out

		double outputLeft = left.calculate(getDistance_ticks(lTalon));
		double outputRight = right.calculate(getDistance_ticks(rTalon));
		System.out.println(outputLeft);

		System.out.println(outputLeft);
		
		

		SmartDashboard.putNumber("left encoder distance", getDistance_ticks(lTalon));
		SmartDashboard.putNumber("Right encoder distance", getDistance_ticks(rTalon));
		

		double gyro_heading =  ahrs.getYaw();  // Assuming the gyro is giving a value in degrees

		SmartDashboard.putNumber("robot yaw", gyro_heading);
		
		double desired_heading = (180/Math.PI)*(left.getHeading());  // Should also be in degrees

		double angleDifference = (desired_heading - gyro_heading);
		double turn = 0.8 * (-1.0/80.0) * angleDifference;// dont understand 

		double setLeftMotors= outputLeft+turn ;
		double setRightMotors = outputRight - turn ;
		// debug test 
		// If (L>R){
	//Sysem.out.println(	setLeftMotors);
//	}
		 
		// need to graph 
		SmartDashboard.putNumber(" vLeft", setLeftMotors);
		SmartDashboard.putNumber(" vRight", setRightMotors);
		
		
		// make sure less than -1 
		lTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, outputLeft);
		rTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, outputRight);

	}
}
