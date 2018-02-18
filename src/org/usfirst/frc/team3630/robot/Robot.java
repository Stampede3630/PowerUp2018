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
	BoxGrabber box ;
	public enum StartingPoints {
		LEFT, RIGHT, MIDDLE
	}
	public enum Destinations {
		SWL, SCL, SWR, SCR, DONO, DRFW
	}

	SendableChooser autoChooser;
	SendableChooser autoLLL;
	SendableChooser autoRRR;
	SendableChooser autoLRL;
	SendableChooser autoRLR;
	//DriveTrain _driveTrain;
	//Timer  autoTime; 
	
	
	public void robotInit() {
	
		box = new BoxGrabber();
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
	
	public void teleopPeriodic() {
	
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
	public void disabledPeriodic() {
	
	}

	public void autonomousPeriodic() {
	
	}
	
		
	}




