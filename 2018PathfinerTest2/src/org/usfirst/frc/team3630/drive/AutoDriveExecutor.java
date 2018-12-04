package org.usfirst.frc.team3630.drive; 
//All this stuff will later be in a separate method - I just had to move some stuff from modified Pathfinder class to somewhere else

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;
import org.usfirst.frc.team3630.robot.Constants;
import org.usfirst.frc.team3630.robot.Robot;
import org.usfirst.frc.team3630.robot.RobotMap;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.*;
import jaci.pathfinder.modifiers.TankModifier;

import java.io.File;


public class AutoDriveExecutor 
{
	EncoderFollower leftF, rightF;
	double motorOutputL, motorOutputR, gyroHeading, desiredHeading, angleDifference, turn;
	TankModifier modifier;
	Trajectory left, right;
	Trajectory.Segment seg;
	Trajectory trajectory;
	Waypoint[] waypoints;
	Trajectory.Config config;
	public AHRS ahrs;
	
	public void driveInit(Waypoint[] waypoints) //inits how to execute the driving part
	{
		ahrs = new AHRS(SPI.Port.kMXP);
		File file = new File("/Users/dasevo/eclipse-workspace/PathGenerator/path.csv");
//		createTrajectory(); //creates trajectory
		loadTrajectory(file);
		modifyTrajectory(Constants.wheelBase); //creates modified Trajectory for tank drive
		leftF = new EncoderFollower(left); //something that is comparing the encoder values to the pre-calculated values
        rightF = new EncoderFollower(right);
        
        leftF.configureEncoder(RobotMap.getTicks(RobotMap.twoL), Constants.ticksPerRotation, in2m(Constants.wheelRadius*2)); //sets all values for calculations for distance
        rightF.configureEncoder(RobotMap.getTicks(RobotMap.oneR), Constants.ticksPerRotation, in2m(Constants.wheelRadius*2));
        
        leftF.configurePIDVA(Constants.kP, Constants.kI, Constants.kD, 1/Constants.maxSpeed, 0.0); // 0.0 initialization of PID -> play a bit with the values
        rightF.configurePIDVA(Constants.kP, Constants.kI, Constants.kD, 1/Constants.maxSpeed, 0.0); //0.0
        Pathfinder.writeToCSV(file, trajectory);
	}
	
	public void execute() //the problem is somewhere here
	{
		motorOutputL = leftF.calculate(RobotMap.twoL.getSelectedSensorPosition()); //calculates current speed of a side
        motorOutputR = rightF.calculate(RobotMap.oneR.getSelectedSensorPosition());
        gyroHeading = ahrs.getAngle();
        SmartDashboard.putNumber("degree", ahrs.getAngle());
        desiredHeading = Pathfinder.r2d(leftF.getHeading());
        angleDifference = Pathfinder.boundHalfDegrees(desiredHeading+gyroHeading); //for some reason, this should work better
        turn = 0.8*(-1.0/80.0)*angleDifference; //not my calculations -> something that defines the heading
        double[] leftRight = boundOutput(motorOutputL+turn, motorOutputR-turn);
        RobotMap.drive.tankDrive(leftRight[0], leftRight[1]); //processing of the speeds in tankDrive
        System.out.println((motorOutputL+turn) + "|||||" + (motorOutputR-turn) + "|||||" + leftRight[0] + "|||||" + leftRight[1]);
	}
	
	public void modifyTrajectory(double wheelbase) {
        modifier = new TankModifier(trajectory); //creates modifier

        modifier.modify(wheelbase); //adjusts the modifier for our specific chassis

        left = modifier.getLeftTrajectory();
        right = modifier.getRightTrajectory(); //sets the trajectories of left and right side
    }
	
	public void createTrajectory() { //input is in in and degrees
        waypoints = new Waypoint[] {new Waypoint(0.0, 0.0, 0.0), new Waypoint(0.75, -0.75,Pathfinder.d2r(135)), new Waypoint(2.0, -1.0, Pathfinder.d2r(0))};
        //new Waypoint(0.5, 0.5, Pathfinder.d2r(-45)), new Waypoint(1, 1, Pathfinder.r2d(0)),
        
 /*       for (int i = 0; i<angles.length; i++)
        {
        	waypoints[i] = new Waypoint(in2m(xCoords[i]), in2m(yCoords[i]), Robot.pathfinder.d2r(angles[i])); //sets an array of waypoints
        	//the pathfinder intakes radians and we wand to input degrees
        }
        */     
        config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, Constants.maxSpeed, 2.6, 60.0);
        trajectory = Pathfinder.generate(waypoints, config); //PROBLEM
        for (int i = 0; i < trajectory.length(); i++) {
            seg = trajectory.get(i);
            
            System.out.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
                seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
                    seg.acceleration, seg.jerk, seg.heading); //prints values for each segment of the trajectory
        }
        ahrs.reset();
    }
	
	public static double[] boundOutput(double left, double right)
	{
		double[] toReturn = new double[2];
		if(Math.abs(left)<=1&&Math.abs(right)<=1)
		{
			toReturn[0] = left;
			toReturn[1] = right;
		}
		else if(Math.abs(left)>1&&Math.abs(left)>Math.abs(right))
		{
			toReturn[0] = (left/Math.abs(left));
			toReturn[1] = (right/Math.abs(left));
		}
		else if(Math.abs(right)>1&&Math.abs(right)>Math.abs(left))
		{
			toReturn[0] = (left/Math.abs(right));
			toReturn[1] = (right/Math.abs(right));
		}
		else
		{
			toReturn[0] = 0;
			toReturn[1] = 0;
		}
		return toReturn;
	}
	
	public static double in2m(double in) //converting from in to m -> pathfinder intakes m and radians
	{
		return in*0.0254;
	}
	
	public static double m2in(double m)
	{
		return m*39.3701;
	}
	
	public void loadTrajectory(File file)
	{
		trajectory = Pathfinder.readFromCSV(file);
	}
}
