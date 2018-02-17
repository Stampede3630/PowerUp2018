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
@SuppressWarnings({"rawtypes", "unchecked"})
public class Robot extends IterativeRobot {
	String gameData;
	public enum StartingPoints {
		LEFT, RIGHT, MIDDLE, UNDEFINED
	}
	
	public enum Destinations {
		SWL, SCL, SWR, SCR, DONO, DRFW
	}
	
	StartingPoints mySP;
	Destinations myDest;

	SendableChooser autoChooser;
	SendableChooser autoLLL;
	SendableChooser autoRRR;
	SendableChooser autoLRL;
	SendableChooser autoRLR;
	DriveTrain _driveTrain;
	//Timer  autoTime; 
	
	@Override
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
		autoLLL.addDefault("Switch", Destinations.SWL);
		autoLLL.addObject("Scale", Destinations.SCL);
		autoLLL.addObject("Do Nothing", Destinations.DONO);
		autoLLL.addObject("Auto Line", Destinations.DRFW);
		SmartDashboard.putData("LLL Options", autoLLL);
		
		autoRRR = new SendableChooser();
		autoRRR.addDefault("Switch", Destinations.SWR);
		autoRRR.addObject("Scale", Destinations.SCR);
		autoRRR.addObject("Do Nothing", Destinations.DONO);
		autoRRR.addObject("Auto Line", Destinations.DRFW);
		SmartDashboard.putData("RRR Options", autoRRR);
		
		autoLRL = new SendableChooser();
		autoLRL.addDefault("Switch", Destinations.SWL);
		autoLRL.addObject("Scale", Destinations.SCR);
		autoLRL.addObject("Do Nothing", Destinations.DONO);
		autoLRL.addObject("Auto Line", Destinations.DRFW);
		SmartDashboard.putData("LRL Options", autoLRL);
		
		autoRLR = new SendableChooser();
		autoRLR.addDefault("Switch", Destinations.SWR);
		autoRLR.addObject("Scale", Destinations.SCL);
		autoRLR.addObject("Do Nothing", Destinations.DONO);
		autoRLR.addObject("Auto Line", Destinations.DRFW);
		SmartDashboard.putData("RLR Options", autoRLR);
	}
	@Override
	public void robotPeriodic() {
		_driveTrain.getDiagnostics();
		
		mySP = (StartingPoints) autoChooser.getSelected();
	}
	
	@Override
	public void teleopInit() {
	}
	@Override
	public void teleopPeriodic() {
		_driveTrain.teleopPeriodic();
	}
	@Override
	public void autonomousInit() {
		//autoTime.reset();
		//autoTime.start();
		_driveTrain.autoInit();
		gameData = DriverStation.getInstance().getGameSpecificMessage();

		//_driveTrain.driveStraight();
		SmartDashboard.putString("auto starting position", autoChooser.getSelected().toString());
	}
	@Override
	public void autonomousPeriodic() {
		autoLogic();
		_driveTrain.getDiagnostics();
	}
	@Override
	public void disabledInit() {
	}
	@Override
	public void disabledPeriodic() {
		_driveTrain.putData();
	}


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
			myDest = (Destinations) autoLLL.getSelected();
			
			switch (mySP) {
			case LEFT:
				switch (myDest) {
				case SWL:
					_driveTrain.leftSwitchLeft();
					break;
				case SCL:
					_driveTrain.leftScaleLeft();
					break;
				case SWR:
					_driveTrain.leftSwitchRight();
					break;
				case SCR:
					_driveTrain.leftScaleRight();
					break;
				case DONO:
					_driveTrain.autoDoNothing();
					break;
				case DRFW:
					_driveTrain.autoDriveFw(Consts.autoLine);
					break;
				default:
					_driveTrain.autoDriveFw(Consts.autoLine);
				}
				break;
			case MIDDLE:
				switch (myDest) {
				case SWL:
					_driveTrain.middleSwitchLeft();
					break;
				case SCL:
					_driveTrain.middleScaleLeft();
					break;
				case SWR:
					_driveTrain.middleSwitchRight();
					break;
				case SCR:
					_driveTrain.middleScaleRight();
					break;
				case DONO:
					_driveTrain.autoDoNothing();
					break;
				case DRFW:
					_driveTrain.autoDriveFw(Consts.autoLine);
					break;
				default:
					_driveTrain.autoDriveFw(Consts.autoLine);
				}
				break;
			case RIGHT:
				switch (myDest) {
				case SWL:
					_driveTrain.rightSwitchLeft();
					break;
				case SCL:
					_driveTrain.rightScaleLeft();
					break;
				case SWR:
					_driveTrain.rightSwitchRight();
					break;
				case SCR:
					_driveTrain.rightScaleRight();
					break;
				case DONO:
					_driveTrain.autoDoNothing();
					break;
				case DRFW:
					_driveTrain.autoDriveFw(Consts.autoLine);
					break;
				default:
					_driveTrain.autoDriveFw(Consts.autoLine);
				}
				break;
			default:
				break;
			}
			break;
		case "LR":
			myDest = (Destinations) autoLRL.getSelected();
			break;
		case "RR":
			myDest = (Destinations) autoRRR.getSelected();
			break;
		case "RL":
			myDest = (Destinations) autoRLR.getSelected();
			break;
		}
	}
}