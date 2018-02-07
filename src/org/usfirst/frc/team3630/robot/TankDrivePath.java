package org.usfirst.frc.team3630.robot;


import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.followers.DistanceFollower;
import jaci.pathfinder.modifiers.TankModifier;
import com.ctre.phoenix.*;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

// debug checklist: 
// confirm talon setpoint between -1 and 1 
// confirm units 
// sensor ticks in , -1, 1 output 

// check max acceloration and jerk make sence. Probobly talk mr lampe about that 
// check waypoints are generated properly and make sence 
// graph in excel 
// is the talon kp ki kd constans competing with the pivda in pathfinder? 

// deferntial debuging 
// probobly not kp problem
// a units problem? 
// outputing enough 
// pathfinder libary problem 
// is everything init properly? looks ok to me. will look though libary though 
// why dose pathfinder stop when we don't think we got to setpoint right place. Makes me think units problem

public class TankDrivePath {
	// Trajectory right,left;\
	TankModifier _modifier;
	Trajectory leftTrajectory;
	Trajectory rightTrajectory;
	private TalonSRX lTalon;
	private TalonSRX rTalon;
	EncoderFollower left, right;
	DistanceFollower leftDiagnostics, rightDiagnostics;

	public TankDrivePath(TalonSRX leftSRXSide, TalonSRX rightSRXSide) {


		lTalon = leftSRXSide;
		rTalon = rightSRXSide;
		
		
		// Create the Trajectory Configuration
		//
		// Arguments:
		// Fit Method: HERMITE_CUBIC or HERMITE_QUINTIC
		// Sample Count: SAMPLES_HIGH (100 000)
		// SAMPLES_LOW (10 000)
		// SAMPLES_FAST (1 000)
		// Time Step: 0.05 Seconds
		// Max Velocity: 45 in/sec
		// Max Acceleration: 100 in/s/s
		// Max Jerk: 100 in/s/s/s

		// comsts ok???
		// should we modlify time step
		
		//Settings for trajectory config
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC,
				Trajectory.Config.SAMPLES_HIGH, 0.05, 3.3528, 2.54, 2.54);

		//Generates points for the path.
		Waypoint[] points = new Waypoint[] {
				// new Waypoint(-4, -1, Pathfinder.d2r(-45)),
				new Waypoint(0, 0, 0),
				new Waypoint(4.2672, 0, 0) //14 feet forward should clock in 8,000 clicks way undeer 
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
		left.configureEncoder(0, Consts.ticksPerRotation, 0.1524);
		right.configureEncoder(0, Consts.ticksPerRotation, 0.1524);
		// outputs data from path put to csv
		// lets produce trajectory graphs. to check conttants are correct ?
		// pos and velocity per time
		// acceloration per time
		// any other graphs?
	// generate path
		 //OUTPUTS PATH
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

	

		// mabey reset kp
		  // could we be under dampond 
		  
		left.configurePIDVA(1, 0.0, 0.0, (1/3.3528) , 0);
	right.configurePIDVA(1, 0.0, 0.0, (1/3.3528), 0);
		
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
/*
	public double getVelocity_talonSpeed(double inPerSec) {
	//	double rads = inPerSec * Consts.rotConversionMeters;
		//double ticks = rads * 1000;
		//double ms = (ticks / 1000) * 100;
		//return ms;
	}
*/

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
/*
		System.out.println(outputLeft) ;

		
		double distance_coveredLeft = ((double) (getDistance(lTalon)) / 1000) * (6 * Math.PI);
		double leftError = left.getSegment().position - distance_coveredLeft;
		
		// very large and add to number 
		double pPart = .005 * leftError;
		double leftVelocity = (1 / 135) * left.getSegment().velocity;
		double leftAcceloratin= left.getSegment().acceleration; 
		*/
		System.out.println(outputLeft);
		
		//double calculated_value = pPart + leftVelocity;
/*
		SmartDashboard.putNumber("left output ", outputLeft);
		SmartDashboard.putNumber("right output ", outputRight);
		SmartDashboard.putBoolean("finished?", left.isFinished());
		SmartDashboard.putNumber("time", Timer.getMatchTime());
/*
		SmartDashboard.putNumber("leftError ", leftError);
		SmartDashboard.putNumber("pPartLeft ", pPart);
		SmartDashboard.putNumber("LeftCalcualtedValue ", calculated_value);
		SmartDashboard.putNumber("Left encoder setpoiny  ", distance_coveredLeft);
		SmartDashboard.putNumber("LeftVelocitysetpoint ", leftVelocity);
		*/
		SmartDashboard.putNumber("left encoder distance", getDistance_ticks(lTalon));
		SmartDashboard.putNumber("Right encoder distance", getDistance_ticks(rTalon));
		
		  
	/*
	 * 
	 */
		 
		//SmartDashboard.putNumber(" vLeft", left.getSegment().velocity);
		// setpint needs to be petween -1 and 1 need to confirm
		// are we feeding pathfinder enoughpoints
		// are th
		lTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, outputLeft);
		rTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, outputRight);

	}
}
