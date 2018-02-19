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
	//DriveTrain _driveTrain;
	//Timer  autoTime; 
	
	@Override
	public void robotInit() {
		
		box = new BoxGrabber();
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		autoChooser = new SendableChooser<StartingPoints>();
		autoChooser.addDefault("left", StartingPoints.LEFT);
		autoChooser.addObject("right", StartingPoints.RIGHT);
		autoChooser.addObject("middle", StartingPoints.MIDDLE);
		SmartDashboard.putData("Starting Position", autoChooser);
		
		//autoTime = new Timer();
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
	
	public void testInit() {
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
		_driveTrain.teleopPeriodic()
		box.boxGraberPeriodic();
	}
	public void autonomousInit() {
		//autoTime.reset();
		//autoTime.start();

		//gameData = DriverStation.getInstance().getGameSpecificMessage();

		//_driveTrain.driveStraight();
		//2SmartDashboard.putString("auto starting position", autoChooser.getSelected().toString());
	}
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
	
	}

		if(gameData.length() > 0) {
			
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
}