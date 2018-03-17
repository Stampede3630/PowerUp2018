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
public class Robot extends TimedRobot {
	String gameData;
	BoxGrabber box ;

	public enum StartingPoints {
		LEFT, RIGHT, MIDDLE, UNDEFINED
	}
	public enum Destinations {
		SWL, SCL, SWR, SCR, DONO, DRFW
	}

	StartingPoints mySP;
	Destinations myDest;

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
		_driveTrain.pathTwo.pathDiog();
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
		

		gameData = DriverStation.getInstance().getGameSpecificMessage();
}
	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.IterativeRobotBase#autonomousPeriodic()
	 * irative method for auto. calls all methods during auto that are used 
	 */
	@Override
	public void autonomousPeriodic() {

		_driveTrain.autoPeriodic();
		
		_driveTrain.getDiagnostics();
		box.switchAutoUpPeriodic();

		box.liftUpPeriodic();
	}
	@Override
	public void disabledInit() {
	}
	
	@Override
	public void disabledPeriodic() {
		
	}

	/**
	 * deals with game data from fms which gets sent to us and set auto rutube
	 */
	
}

