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
	String gameData;
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
		
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		autoChooser = new SendableChooser();
		autoChooser.addDefault("left", StartingPoints.LEFT);
		autoChooser.addObject("right", StartingPoints.RIGHT);
		autoChooser.addObject("middle", StartingPoints.MIDDLE);
		SmartDashboard.putData("Starting Position", autoChooser);
		
		//autoTime = new Timer();
		autoLLL = new SendableChooser();
		autoLLL.addDefault("Switch", LLLResMet.SWL);
		autoLLL.addObject("Scale", LLLResMet.SCL);
		SmartDashboard.putData("LLL Options", autoLLL);
		
		autoRRR = new SendableChooser();
		autoRRR.addDefault("Switch", RRRResMet.SWR);
		autoRRR.addObject("Scale", RRRResMet.SCR);
		SmartDashboard.putData("RRR Options", autoRRR);
		
		autoLRL = new SendableChooser();
		autoLRL.addDefault("Switch", LRLResMet.SWL);
		autoLRL.addObject("Scale", LRLResMet.SCR);
		SmartDashboard.putData("LRL Options", autoLRL);
		
		autoRLR = new SendableChooser();
		autoRLR.addDefault("Switch", RLRResMet.SWR);
		autoRLR.addObject("Scale", RLRResMet.SCL);
		SmartDashboard.putData("RLR Options", autoRLR);
	}
	
	public void teleopPeriodic() {
		_driveTrain.driveTrainPeriodic();
		
	}
	public void autonomousInit() {
		//autoTime.reset();
		//autoTime.start();
		_driveTrain.testInit();
		gameData = DriverStation.getInstance().getGameSpecificMessage();

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

	public void autonomousPeriodic() {
		autoLogic();
		_driveTrain.testDriveTrainPeriodic();
	}
	public void autoLogic() {
		if(gameData.length() > 0) {
			
			if((gameData.charAt(0) == 'L') && (gameData.charAt(1) == 'L')) {
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
			}
			else if((gameData.charAt(0) == 'R') && (gameData.charAt(1) == 'R')) {
				if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoRRR.getSelected() == RRRResMet.SWR)) {
					_driveTrain.leftSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoRRR.getSelected() == RRRResMet.SCR)) {
					_driveTrain.leftScaleRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoRRR.getSelected() == RRRResMet.SWR)) {
					_driveTrain.rightSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoRRR.getSelected() == RRRResMet.SCR)) {
					_driveTrain.rightScaleRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoRRR.getSelected() == RRRResMet.SWR)) {
					_driveTrain.middleSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoRRR.getSelected() == RRRResMet.SCR)) {
					_driveTrain.middleScaleRight();	
				}
			}
			else if((gameData.charAt(0) == 'L') && (gameData.charAt(1) == 'R')) {
				if ((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLRL.getSelected() == LRLResMet.SWL)) {
					_driveTrain.leftSwitchLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLRL.getSelected() == LRLResMet.SCR)) {
					_driveTrain.leftScaleRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLRL.getSelected() == LRLResMet.SWL)) {
					_driveTrain.rightSwitchLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLRL.getSelected() == LRLResMet.SCR)) {
					_driveTrain.rightScaleRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLRL.getSelected() == LRLResMet.SWL)) {
					_driveTrain.middleSwitchLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLRL.getSelected() == LRLResMet.SCR)) {
					_driveTrain.middleScaleRight();
				}
			}
			else if((gameData.charAt(0) == 'R') && (gameData.charAt(1) == 'L')) {
				if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoRLR.getSelected() == RLRResMet.SWR)) {
					_driveTrain.leftSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoRLR.getSelected() == RLRResMet.SCL)) {
					_driveTrain.leftScaleLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoRLR.getSelected() == RLRResMet.SWR)) {
					_driveTrain.rightSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoRLR.getSelected() == RLRResMet.SCL)) {
					_driveTrain.rightScaleLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoRLR.getSelected() == RLRResMet.SWR)) {
					_driveTrain.middleSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoRLR.getSelected() == RLRResMet.SCL)) {
					_driveTrain.middleScaleLeft();	
				}
			}
			else {
				_driveTrain.middleSwitchRight();
			}
		}
	}

}


