package org.usfirst.frc.team3630.robot;


import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
//should we change to timed robot ? 
public class Robot extends IterativeRobot {
	String gameData;
	BoxGrabber box ;

	public enum StartingPoints {
		LEFT, RIGHT, MIDDLE
	}
	public enum Destinations {
		SWL, SCL, SWR, SCR, DONO, DRFW
	}

	SendableChooser<StartingPoints> autoChooser;
	SendableChooser<Destinations> autoLLL;
	SendableChooser<Destinations> autoRRR;
	SendableChooser<Destinations> autoLRL;
	SendableChooser<Destinations> autoRLR;
	DriveTrain _driveTrain;

	
	@Override
	public void robotInit() {
	
		box = new BoxGrabber();
		_driveTrain = new DriveTrain(box);
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		autoChooser = new SendableChooser<StartingPoints>();
		autoChooser.addDefault("left", StartingPoints.LEFT);
		autoChooser.addObject("right", StartingPoints.RIGHT);
		autoChooser.addObject("middle", StartingPoints.MIDDLE);
		SmartDashboard.putData("Starting Position", autoChooser);
		
		autoLLL = new SendableChooser<Destinations>();
		autoLLL.addDefault("Switch", Destinations.SWL);
		autoLLL.addObject("Scale", Destinations.SCL);
		autoLLL.addObject("Do Nothing", Destinations.DONO);
		autoLLL.addObject("Auto Line", Destinations.DRFW);
		SmartDashboard.putData("LLL Options", autoLLL);
		
		autoRRR = new SendableChooser<Destinations>();
		autoRRR.addDefault("Switch", Destinations.SWR);
		autoRRR.addObject("Scale", Destinations.SCR);
		autoRRR.addObject("Do Nothing", Destinations.DONO);
		autoRRR.addObject("Auto Line", Destinations.DRFW);
		SmartDashboard.putData("RRR Options", autoRRR);
		
		autoLRL = new SendableChooser<Destinations>();
		autoLRL.addDefault("Switch", Destinations.SWL);
		autoLRL.addObject("Scale", Destinations.SCR);
		autoLRL.addObject("Do Nothing", Destinations.DONO);
		autoLRL.addObject("Auto Line", Destinations.DRFW);
		SmartDashboard.putData("LRL Options", autoLRL);
		
		autoRLR = new SendableChooser<Destinations>();
		autoRLR.addDefault("Switch", Destinations.SWR);
		autoRLR.addObject("Scale", Destinations.SCL);
		autoRLR.addObject("Do Nothing", Destinations.DONO);
		autoRLR.addObject("Auto Line", Destinations.DRFW);
		SmartDashboard.putData("RLR Options", autoRLR);
		}
	
	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.IterativeRobotBase#testInit()
	 * init method for test modde 
	 */
	public void testInit() {
		// should we empty out test if we aren't useing it anymore 
		_driveTrain.testInit();
	}
	
	public void testPeriodic() {
		_driveTrain.testPeriodic();
	}
	
	@Override
	public void robotPeriodic() {
		_driveTrain.getDiagnostics();
	}
	
	@Override
	public void teleopInit() {
		_driveTrain.teleopInit();
	}
	@Override
	public void teleopPeriodic() {
		_driveTrain.teleopPeriodic();
		box.boxGrabberPeriodic();
	}
	public void autonomousInit() {
		
		_driveTrain.autoInit();
		gameData = DriverStation.getInstance().getGameSpecificMessage();
}
	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.IterativeRobotBase#autonomousPeriodic()
	 * irative method for auto. calls all methods during auto that are used 
	 */
	@Override
	public void autonomousPeriodic() {
		autoLogic();
		_driveTrain.autoPeriodic();
		_driveTrain.getDiagnostics();
	}
	@Override
	public void disabledInit() {
	}
	
	@Override
	public void disabledPeriodic() {
		_driveTrain.putData();
	}

	/**
	 * deals with game data from fms which gets sent to us and set auto rutube
	 */
	public void autoLogic() {
		if(gameData.length() > 0) {
			
			if((gameData.charAt(0) == 'L') && (gameData.charAt(1) == 'L')) {
				if ((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLLL.getSelected() == Destinations.SWL)) {
					_driveTrain.leftSwitchLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLLL.getSelected() == Destinations.SCL)) {
					_driveTrain.leftScaleLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLLL.getSelected() == Destinations.SWL)) {
					_driveTrain.rightSwitchLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLLL.getSelected() == Destinations.SCL)) {
					_driveTrain.rightScaleLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLLL.getSelected() == Destinations.SWL)) {
					_driveTrain.middleSwitchLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLLL.getSelected() == Destinations.SCL)) {
					_driveTrain.middleScaleLeft();
				}
				else if(autoLLL.getSelected() == Destinations.DONO) {
					_driveTrain.autoDoNothing();
				}
				else if(autoLLL.getSelected() == Destinations.DRFW) {
					_driveTrain.autoDriveFw(Consts.autoLine);
				}
			}
			else if((gameData.charAt(0) == 'R') && (gameData.charAt(1) == 'R')) {
				if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoRRR.getSelected() == Destinations.SWR)) {
					_driveTrain.leftSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoRRR.getSelected() == Destinations.SCR)) {
					_driveTrain.leftScaleRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoRRR.getSelected() == Destinations.SWR)) {
					_driveTrain.rightSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoRRR.getSelected() == Destinations.SCR)) {
					_driveTrain.rightScaleRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoRRR.getSelected() == Destinations.SWR)) {
					_driveTrain.middleSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoRRR.getSelected() == Destinations.SCR)) {
					_driveTrain.middleScaleRight();	
				}
				else if(autoRRR.getSelected() == Destinations.DONO) {
					_driveTrain.autoDoNothing();
				}
				else if(autoRRR.getSelected() == Destinations.DRFW) {
					_driveTrain.autoDriveFw(Consts.autoLine);
				}
			}
			else if((gameData.charAt(0) == 'L') && (gameData.charAt(1) == 'R')) {
				if ((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLRL.getSelected() == Destinations.SWL)) {
					_driveTrain.leftSwitchLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoLRL.getSelected() == Destinations.SCR)) {
					_driveTrain.leftScaleRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLRL.getSelected() == Destinations.SWL)) {
					_driveTrain.rightSwitchLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoLRL.getSelected() == Destinations.SCR)) {
					_driveTrain.rightScaleRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLRL.getSelected() == Destinations.SWL)) {
					_driveTrain.middleSwitchLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoLRL.getSelected() == Destinations.SCR)) {
					_driveTrain.middleScaleRight();
				}
				else if(autoLRL.getSelected() == Destinations.DONO) {
					_driveTrain.autoDoNothing();
				}
				else if(autoLRL.getSelected() == Destinations.DRFW) {
					_driveTrain.autoDriveFw(Consts.autoLine);
				}
			}
			else if((gameData.charAt(0) == 'R') && (gameData.charAt(1) == 'L')) {
				if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoRLR.getSelected() == Destinations.SWR)) {
					_driveTrain.leftSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.LEFT) && (autoRLR.getSelected() == Destinations.SCL)) {
					_driveTrain.leftScaleLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoRLR.getSelected() == Destinations.SWR)) {
					_driveTrain.rightSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.RIGHT) && (autoRLR.getSelected() == Destinations.SCL)) {
					_driveTrain.rightScaleLeft();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoRLR.getSelected() == Destinations.SWR)) {
					_driveTrain.middleSwitchRight();
				}
				else if((autoChooser.getSelected() == StartingPoints.MIDDLE) && (autoRLR.getSelected() == Destinations.SCL)) {
					_driveTrain.middleScaleLeft();	
				}
				else if(autoRLR.getSelected() == Destinations.DONO) {
					_driveTrain.autoDoNothing();
				}
				else if(autoRLR.getSelected() == Destinations.DRFW) {
					_driveTrain.autoDriveFw(Consts.autoLine);
				}
			}
			else {
				_driveTrain.autoDoNothing();
			}
		}
	}

	public void caseAutoLogic() {
		switch(gameData.substring(0, 1)) {
		case "LL":
			break;
		case "LR":
			break;
		case "RR":
			break;
		case "RL":
			break;
		}
		
	}
}
