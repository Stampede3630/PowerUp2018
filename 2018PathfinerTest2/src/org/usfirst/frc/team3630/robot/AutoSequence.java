package org.usfirst.frc.team3630.robot;

import jaci.pathfinder.Waypoint;

public class AutoSequence 
{
	double[] x, y, angle;
	double[][] points;
	Waypoint[][] waypoints;
	public void basicSequence()
	{
		init(0);
		Robot.autoDriveExecutor.driveInit(waypoints);
	}
	
	public void init(int size)
	{
		waypoints = new Waypoint[size][3];
	}
	
	public void straight()
	{
		init(2);
//		points[0] = [];
		x[0] = 0;
		x[1] = 0.6;
		y[0] = 0;
		y[0] = 0;
		angle[0] = 0;
		angle[1] = 0;
	}
}
