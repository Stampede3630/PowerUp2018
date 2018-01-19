package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	DriveTrain _driveTrain;
	public void robotInit() {
		_driveTrain = new DriveTrain();
		
	}
	public void teleopPeriodic() {
		_driveTrain.driveTrainPeriodic();
		
	}
	public void testPeriodic() {
		_driveTrain.testPeriodic();
	}
}

