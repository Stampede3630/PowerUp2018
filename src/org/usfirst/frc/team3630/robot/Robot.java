package org.usfirst.frc.team3630.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	public enum StartingPoints {
		LEFT	, RIGHT, MIDDLE
	}
	public enum LLLResMet {
		SWL, SCL 
	}
	public enum RRRResMet {
		SWR, SCR
	}
	public enum LRLResMet {
		SWL, SCR
	}
	public enum RLRResMet {
		SWR, SCL
	}
	SendableChooser autoChooser;
	SendableChooser autoLLL;
	SendableChooser autoRRR;
	SendableChooser autoLRL;
	SendableChooser autoRLR;
	DriveTrain _driveTrain;
	//Timer  autoTime; 
	
	public void robotInit() {
	_driveTrain = new DriveTrain();
	autoChooser = new SendableChooser();
	autoChooser.addDefault("left", StartingPoints.LEFT);
	autoChooser.addObject("right", StartingPoints.RIGHT);
	autoChooser.addObject("middle", StartingPoints.MIDDLE);
	SmartDashboard.putData("Starting Position", autoChooser);
	//autoTime = new Timer();
	autoLLL = new SendableChooser();
	autoLLL.addDefault("Switch," LLLResMet.SWL);
	autoLLL.addObject("Scale," LLLResMet.SCL);
	SmartDashboard.putData("LLL Options", autoLLL);
	
	autoRRR = new SendableChooser();
	autoRRR.addDefault("Switch," RRRResMet.SWR);
	autoRRR.addObject("Scale," RRRResMet.SCR);
	SmartDashboard.putData("RRR Options", autoRRR);
	
	autoLRL = new SendableChooser();
	autoLRL.addDefault("Switch", LRLResMet.SWL);
	autoLRL.addObject("Scale", LRLResMet.SCR);
	SmartDashboard.putData("LRL options", autoLRL);
	
	autoRLR = new SendableChooser();
	autoRLR.addDefault("Switch", RLRResMet.);
	}
	
	public void teleopPeriodic() {
		_driveTrain.driveTrainPeriodic();
		
	}
	@Override
	public void testInit() {
		//autoTime.reset();
		//autoTime.start();
		_driveTrain.testInit();
		//_driveTrain.driveStraight();
		SmartDashboard.putString("auto starting position", autoChooser.getSelected().toString());
		SmartDashboard.putString("Auto LLL", autoLLL.getSelected().toString());
	}
	@Override
	public void disabledPeriodic() {
		_driveTrain.putData();
	}
	/*public void DriveAngle() {
		
		// rest navx
		_driveTrain.testInit();
		
		SmartDashboard.putNumber("timer", autoTime.get());
		_driveTrain.putData();
	
		
		if (	autoTime.get()< 6) {
			
			// go straight
			_driveTrain.testDriveTrainPeriodic();
	}

		else if (autoTime.get()<15 ) {
			// turn 90 degrees 
			_driveTrain.turnDegree(90f);
			
			_driveTrain.testDriveTrainPeriodic();
			
		}
		
	
		
		else {
			
			// stop 
			_driveTrain.stop();
			
		}
			
		}
		*/
	@Override
	public void testPeriodic() {
		autoLogic();
		_driveTrain.testDriveTrainPeriodic();
	}
	public void autoLogic() {
		if ((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLLL.getSelected() == LLLResMet.SWL)) {
			_driveTrain.leftSwitchLeft();
		}
		else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLLL.getSelected() == LLLResMet.SCL)) {
			_driveTrain.leftScaleLeft();
		}
		else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLLL.getSelected() == LLLResMet.SWL)) {
			_driveTrain.rightSwitchLeft();
		}
		else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLLL.getSelected() == LLLResMet.SCL)) {
			_driveTrain.rightScaleLeft();
		}
		else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLLL.getSelected() == LLLResMet.SWL)) {
			_driveTrain.middleSwitchLeft();
		}
		else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLLL.getSelected() == LLLResMet.SCL)) {
			_driveTrain.middleScaleLeft();
		}
		else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLLL.getSelected() == RRRResMet.SWR)) {
			_driveTrain.leftSwitchRight();
		}
		else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLLL.getSelected() == RRRResMet.SCR)) {
			_driveTrain.leftScaleRight();
		}
		else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLLL.getSelected() == RRRResMet.SWR)) {
			_driveTrain.rightSwitchRight();
		}
		else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLLL.getSelected() == RRRResMet.SCR)) {
			_driveTrain.rightScaleRight();
		}
		else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLLL.getSelected() == RRRResMet.SWR)) {
			_driveTrain.middleSwitchRight();
		}
		else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLLL.getSelected() == RRRResMet.SCR)) {
			_driveTrain.middleScaleRight();
		}
		else {
			_driveTrain.middleSwitchRight();
		}
		else if ((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLLL.getSelected() == LRLResMet.SWL)) {
			_driveTrain.leftSwitchLeft();
		}
		else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLLL.getSelected() == LRLResMet.SCR)) {
			_driveTrain.leftScaleRight();
		}
		else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLLL.getSelected() == LRLResMet.SWL)) {
			_driveTrain.rightSwitchLeft();
		}
		else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLLL.getSelected() == LRLResMet.SCR)) {
			_driveTrain.rightScaleRight();
		}
		else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLLL.getSelected() == LRLResMet.SWL)) {
			_driveTrain.middleSwitchLeft();
		}
		else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLLL.getSelected() == LRLResMet.SCR)) {
			_driveTrain.middleScaleRight();
		}
	}

}


