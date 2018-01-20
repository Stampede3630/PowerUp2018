package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	DriveTrain _driveTrain;
	Timer  autoTime; 
	public void robotInit() {
	_driveTrain = new DriveTrain();
	autoTime = new Timer();
	}
	public void teleopPeriodic() {
		//_driveTrain.driveTrainPeriodic();
		
	}
	@Override
	public void testInit() {
		autoTime.reset();
		autoTime.start();
		_driveTrain.autoInit();
		_driveTrain.driveStraight();
	}
	@Override
	public void testPeriodic() {
		SmartDashboard.putNumber("timer", autoTime.get());
		_driveTrain.putData();
		if (	autoTime.get()< 6) {
		
			_driveTrain.testDriveTrainPeriodic();
	}
		else if (autoTime.get()<15 ) {
			_driveTrain.turnDegree(90f);
			
			_driveTrain.testDriveTrainPeriodic();
			
		}
		
	
		
		else {
			_driveTrain.stop();
			
		}
			
		
	}
	
}

