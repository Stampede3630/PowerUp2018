package org.usfirst.frc.team3630.robot;




import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class TankDrivePath {
	
	public TankDrivePath () {
		 Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, 1.7, 2.0, 60.0);
	        Waypoint[] points = new Waypoint[] {
	            //    new Waypoint(-4, -1, Pathfinder.d2r(-45)),
	        		  new Waypoint(0, 0, 0) ,
	        		new Waypoint(100, 0, 0)
	        };

	                Trajectory trajectory = Pathfinder.generate(points, config);

	                // Wheelbase Width = 0.5m
	                TankModifier modifier = new TankModifier(trajectory).modify(0.5);

	                // Do something with the new Trajectories...
	                Trajectory left = modifier.getLeftTrajectory();
	                System.out.println(left.segments);
	                Trajectory right = modifier.getRightTrajectory();
	}
  

    
}
